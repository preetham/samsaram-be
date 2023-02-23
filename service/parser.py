import re
from datetime import datetime
import json
import string
import pandas as pd
from jellyfish import jaro_similarity

from config import settings
from util import constants, log

with open('./data/brands.json', 'r') as f:
    brand_data = json.load(f)

def excel(input_data: bytes, bank: str):
    config = settings.bank_configs[bank]
    skip_rows = calculate_skip_row(input_data, bank=bank)
    log.logger.debug(skip_rows)
    try:
        data_frame = pd.read_excel(input_data, skiprows=skip_rows, usecols=config.COLUMN_RANGE, keep_default_na=False)
        required_fields = data_frame[config.DATA_COLUMNS]
        return required_fields
    except Exception as e:
        log.logger.error(e, exc_info=True)
        raise Exception()


def field_extractor(input_data: pd.DataFrame, bank: str):
    bank_config = settings.bank_configs[bank]
    app_config = settings.app_config
    expense_list = list()
    for idx, series in input_data.iterrows():
        row = series.values
        if not len(row) > 0:
            continue
        if pd.isna(row[0]) or pd.isna(row[1]) or pd.isna(row[2]):
            continue
        if (type(row[2]) == int or type(row[2]) == float) and row[2] <= 0:
            continue
        if type(row[2]) == str and len(row[2].strip()) == 0:
            continue
        mode = ''
        (mode, payment_type, payee) = _info_extractor(row[1], bank)
        if not(payment_type == constants.P2A or payment_type == constants.P2M):
            continue
        category_id = '2'
        if payment_type == constants.P2M:
            brand = brand_extractor(payee)
            if brand:
                transferred_to = dict({
                    'name': brand['name'],
                    'logo': brand['logo'],
                })
                category_id = brand['category_id']
            else:
                transferred_to = dict({
                    'name': payee,
                    'logo': '',
                })
        else:
            transferred_to = dict({
                    'name': payee,
                    'logo': '',
                })
        if type(row[2]) == str:
            amount = float(row[2].strip())
        else:
            amount = float(row[2])
        txn_date = datetime.strptime(row[0].strip(), bank_config.DATE_FORMAT)
        expense = dict({
            'transaction_date': txn_date.strftime(app_config.DATE_OUTPUT_FORMAT),
            'description': row[1],
            'amount': amount,
            'mode': mode,
            'transferred_to': transferred_to,
            'category_id': category_id,
            'payment_type': payment_type
        })
        expense_list.append(expense)
    return expense_list

def _info_extractor(description, bank):
    bank_config = settings.bank_configs[bank]
    method = _info_method_map[bank]
    try:
        return method(description=description, bank_config=bank_config)
    except Exception as e:
        log.logger.debug(e)
        return ('', '', '')

def _sbi_info(description, bank_config):
    pm_regex = re.compile(bank_config.PAYMENT_MODE_REGEX)
    payment_mode_match = pm_regex.search(description)
    if payment_mode_match is None:
        raise ValueError('payment method not supported')
    payment_mode = (payment_mode_match.group()).lower()
    info_extractor_regex = re.compile(bank_config.INFO_REGEX[payment_mode])
    info_match = info_extractor_regex.search(description)
    if info_match is None:
        raise ValueError(constants.ERROR_PAYMENT_INFO)
    mode, vendor, payment_type = '', '', ''
    if payment_mode == constants.UPI:
        (mode, vendor) = info_match.groups()
        payment_type = constants.P2M
    elif payment_mode == constants.INB:
        (mode, payment_type, vendor) = info_match.groups()
        payment_type = payment_type.lower()
    return (mode, payment_type, vendor.strip())

def _icici_info(description, bank_config):
    pm_regex = re.compile(bank_config.PAYMENT_MODE_REGEX)
    payment_mode_match = pm_regex.search(description)
    if payment_mode_match is None:
        raise ValueError('payment method not supported')
    payment_mode = (payment_mode_match.group()).lower().strip()
    if payment_mode == constants.UPI:
        info_regex = re.compile(bank_config.INFO_REGEX[constants.UPI])
        info_match = info_regex.search(description)
        if info_match is None:
            raise ValueError(constants.ERROR_PAYMENT_INFO)
        (mode, payment_to, upi_id) = info_match.groups()
        if mode.strip().lower() == payment_to.strip().lower():
            payment_type = constants.P2A
        else:
            payment_type = constants.P2M
        return (mode, payment_type, upi_id.strip())
    elif payment_mode == constants.VPS or payment_mode == constants.IPS:
        info_regex = re.compile(bank_config.INFO_REGEX[constants.POS])
        info_match = info_regex.search(description)
        if info_match is None:
            raise ValueError(constants.ERROR_PAYMENT_INFO)
        (mode, vendor, location) = info_match.groups()
        return (constants.POS, constants.P2M, vendor.strip() + ' ' + location.strip())
    elif payment_mode == constants.MMT:
        info_regex = re.compile(bank_config.INFO_REGEX[constants.INB])
        info_match = info_regex.search(description)
        if info_match is None:
            raise ValueError(constants.ERROR_PAYMENT_INFO)
        (mode, sub_mode, reason, person) = info_match.groups()
        return (sub_mode, constants.P2A, person.strip())
    return ('', '', '')

def _axis_info(description, bank_config):
    pm_regex = re.compile(bank_config.PAYMENT_MODE_REGEX)
    payment_mode_match = pm_regex.search(description)
    if payment_mode_match is None:
        raise ValueError('payment method not supported')
    payment_mode = (payment_mode_match.group()).lower()
    info_regex = ''     
    if payment_mode in [constants.UPI, constants.IMPS, constants.NEFT]:
        info_regex = re.compile(bank_config.INFO_REGEX[constants.UPI])
        info_match = info_regex.search(description)
        if info_match is None:
            raise ValueError(constants.ERROR_PAYMENT_INFO)
        (mode, payment_type, transfer_to) = info_match.groups()
        return (mode, payment_type.lower(), transfer_to.strip())
    elif payment_mode == constants.INB:
        info_regex = re.compile(bank_config.INFO_REGEX[payment_mode])
        info_match = info_regex.search(description)
        (mode, vendor) = info_match.groups()
        return (mode, constants.P2M, vendor.strip())
    elif payment_mode == constants.POS:
        info_regex = re.compile(bank_config.INFO_REGEX[payment_mode])
        info_match = info_regex.search(description)
        (mode, vendor, location) = info_match.groups()
        return (mode, constants.P2M, vendor.strip() + ' ' + location.strip())
    return ('', '', '')

def brand_extractor(text: str):
    l_text = text.strip().lower().translate(str.maketrans('', '', string.punctuation))
    brand_list = brand_data.keys()
    for brand in brand_list:
        brand_str = brand.strip().lower()
        similarity_score = jaro_similarity(brand_str, l_text)
        if similarity_score >= settings.app_config.SIMILARITY_THRESHOLD:
            return brand_data[brand]
        if brand_str in l_text:
            return brand_data[brand]
        if l_text in brand_str:
            return brand_data[brand]

def calculate_skip_row(input_data: bytes, bank: str):
    config = settings.bank_configs[bank]
    try:
        data_frame = pd.read_excel(input_data, nrows=settings.app_config.READ_ROWS_THRESHOLD, keep_default_na=False)
        for idx, series in data_frame.iterrows():
            row_values = series.values
            for x in row_values:
                if x in config.DATA_COLUMNS:
                    return idx + settings.app_config.READ_ROWS_OFFSET
    except Exception as e:
        log.logger.error(e, exc_info=True)
        return config.SKIP_ROWS

_info_method_map = dict({
    constants.SBI: _sbi_info,
    constants.ICICI: _icici_info,
    constants.AXIS: _axis_info,
})
