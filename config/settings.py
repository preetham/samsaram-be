import os

class _AxisBankConfig(object):
    """Axis Bank specific configuration
    """
    SKIP_ROWS = 15
    SKIP_ROWS_ALTERNATE = 8
    COLUMN_RANGE = None
    DATA_COLUMNS = list(['Tran Date', 'PARTICULARS', 'DR'])
    PAYMENT_MODE_REGEX = '(UPI)|(IMPS)|(NEFT)|(INB)|(POS)'
    INFO_REGEX = dict({
        'upi': '(\w+)\/([P2AM]+)\/\d+\/([\s\w]+)\/.*',
        'inb': '(\w+)\/\d+\/([\w\d\s(.)]+)[\/\w]*',
        'pos': '(\w+)\/([\s\w]+)\/([\s\w]+)[/\d:\w]*',
    })
    DATE_FORMAT = '%d-%m-%Y'

class _SBIConfig(object):
    """SBI specific configuration
    """
    SKIP_ROWS = 20
    SKIP_ROWS_ALTERNATE = 20
    COLUMN_RANGE = None
    DATA_COLUMNS = list(['Txn Date', 'Description', '        Debit'])
    PAYMENT_MODE_REGEX = '(UPI)|(INB)'
    INFO_REGEX = dict({
        'upi': 'TO\sTRANSFER-([\w\s]+)\/[DCRP2A]+\/\d+\/([\s\w]+)\/\w+\/.*',
        'inb': 'TO\sTRANSFER-INB\s([\w]+)\/([DCRP2A]+)\/\d+\/(\w+)--',
    })
    DATE_FORMAT = '%d %b %Y'

class _ICICIConfig(object):
    SKIP_ROWS = 12
    SKIP_ROWS_ALTERNATE = 12
    COLUMN_RANGE = 'B:I'
    DATA_COLUMNS = list(['Transaction Date', 'Transaction Remarks', 'Withdrawal Amount (INR )'])
    PAYMENT_MODE_REGEX = '(UPI)|(VPS)|(IPS)|(MMT)'
    INFO_REGEX = dict({
        'upi': '(\w+)\/\d+\/([\s\w@]+)\/([\d\-.\w@]+).*',
        'pos': '(\w+)\/([\s\w]+)\/\d+\/\d+\/([\w\s]+)',
        'inb': '(\w+)\/(\w+)\/\d+\/([\w\s]+)\/([\s\w]+)\/[\w\s]+'
    })
    DATE_FORMAT = '%d/%m/%Y'

class _AppConfig(object):
    FLASK_ALLOWED_EXTENSIONS = list(['xls', 'xlsx'])
    FLASK_MAX_CONTENT_LENGTH = 10 * 1000 * 1000
    BANK_ALLOWED_VALUES = list(['axis', 'sbi', 'icici'])
    DATE_OUTPUT_FORMAT = '%Y-%m-%d'
    SPLITWISE_CONSUMER_KEY=os.getenv('SPLITWISE_CONSUMER_KEY')
    SPLITWISE_CONSUMER_SECRET=os.getenv('SPLITWISE_CONSUMER_SECRET')
    SPLITWISE_RUPEE_CODE='INR'
    REDIRECT_ENDPOINT='/home'
    SIMILARITY_THRESHOLD=0.8
    APP_HOST='0.0.0.0'
    APP_PORT='8080'

bank_configs = dict({
    "axis": _AxisBankConfig,
    "sbi": _SBIConfig,
    "icici": _ICICIConfig,
})

app_config = _AppConfig