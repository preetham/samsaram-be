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

desc_regex = re.compile(settings.app_config.DESCRIPTION_REGEX, re.IGNORECASE)
am_regex = re.compile(settings.app_config.AMOUNT_REGEX)

def excel(input_data: bytes, bank: str):
    try:
        return txn_extractor(input_data=input_data, bank=bank)
    except Exception as e:
        log.logger.error(e, exc_info=True)
        raise e

def field_extractor(txns: list, bank: str):
    app_config = settings.app_config
    expense_list = list()
    for txn in txns:
        mode = ''
        (mode, payment_type, payee) = _info_extractor(txn['description'], bank)
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
        expense = dict({
            'transaction_date': txn['transaction_date'].strftime(app_config.DATE_OUTPUT_FORMAT),
            'description': txn['description'],
            'amount': txn['amount'],
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

def txn_extractor(input_data: bytes, bank: str):
    txns = list()
    bank_config = settings.bank_configs[bank]
    try:
        data_frame = pd.read_excel(input_data, keep_default_na=False)
        if data_frame is None:
            raise ValueError()
        for i, series in data_frame.iterrows():
            date_idx = ''
            desc_idx = ''
            amount_idx = ''
            txn = dict()
            for idx, entry in series.items():
                if type(entry) == str:
                    entry = entry.strip()
                try:
                    txn_date = datetime.strptime(entry, bank_config.DATE_FORMAT)
                    if txn_date is not None:
                        date_idx = idx
                except Exception as e:
                    pass
                try:
                    desc_match = desc_regex.search(entry)
                    if desc_match is not None:
                        desc_idx = idx
                except Exception as e:
                    pass
                try:
                    amount_str = am_regex.match(entry)
                    if amount_str is not None and len(amount_idx) == 0:
                        amount_idx = idx
                except Exception as e:
                    pass
            if len(date_idx) > 0 and len(desc_idx) > 0 and len(amount_idx) > 0:
                txn['transaction_date'] = datetime.strptime(series.get(date_idx), bank_config.DATE_FORMAT)
                txn['description'] = series.get(desc_idx)
                txn['amount'] = float(series.get(amount_idx))
                txns.append(txn)
        return txns
    except Exception as e:
        log.logger.error(e, exc_info=True)
        raise e

_info_method_map = dict({
    constants.SBI: _sbi_info,
    constants.ICICI: _icici_info,
    constants.AXIS: _axis_info,
})
