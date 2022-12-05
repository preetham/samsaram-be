
from config.settings import app_config
from werkzeug.datastructures import FileStorage

from service import parser

class Preprocess(object):

    def __init__(self, file_obj: FileStorage, bank: str):
        self.file = file_obj
        self.bank = bank
    

    def sanitize(self):
        data = parser.excel(self.file.read(), self.bank)
        expense_list = parser.field_extractor(data, self.bank)
        return expense_list
