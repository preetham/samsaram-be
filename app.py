from flask import Flask, request, jsonify
from werkzeug.datastructures import FileStorage
from flask_cors import CORS

from config.settings import app_config
from service import statement as statement_service
from service import splitwise
from util import constants, log

app = Flask(__name__)
app.config[constants.MAX_CONTENT_LENGTH] = app_config.FLASK_MAX_CONTENT_LENGTH
cors = CORS(app, resources={r'/api/*': {'origins': '*'}})

@app.route('/', methods=[constants.REQUEST_GET])
@app.route('/health', methods=[constants.REQUEST_GET])
def health():
    return dict({'status': 'OK'})

@app.route('/api/v1/login', methods=[constants.REQUEST_GET])
def login():
    domain = request.headers.get(constants.HEADER_ORIGIN)
    url = splitwise.login(domain=domain)
    return url

@app.route('/api/v1/authorize', methods=[constants.REQUEST_POST])
def authorize():
    data = request.get_json()
    code = data[constants.CODE]
    domain = request.headers.get(constants.HEADER_ORIGIN)
    response = splitwise.authorize(code, domain)
    return dict({
        'user': response['user'],
        'token': response['token'],
    })

@app.route('/api/v1/upload', methods=[constants.REQUEST_POST])
def upload():
    file = request.files['statement']
    bank = request.form['bank']
    if not file or not bank:
        return dict({'error': 'missing data'}), 400
    if not isinstance(file, FileStorage):
            raise TypeError(
                'file obj must be type werkzeug.datastructures.FileStorage')
    if not allowed_file(file.filename):
        return dict({'error': 'unsupported file type'}), 400
    if bank not in app_config.BANK_ALLOWED_VALUES:
            raise ValueError('invalid bank value')
    try:
        pre_processor = statement_service.Preprocess(file_obj=file, bank=bank)
        expense_list = pre_processor.sanitize()
        return dict({'data': expense_list})
    except Exception as e:
        log.logger.error(e, exc_info=True)
        return constants.INTERNAL_ERROR_DICT, 500

@app.route('/api/v1/groups', methods=[constants.REQUEST_GET])
def groups():
    try:
        access_token = request.headers.get('Authorization')
        response = splitwise.groups(access_token)
        return response
    except Exception as e:
        log.logger.error(e, exc_info=True)
        return constants.INTERNAL_ERROR_DICT, 500

@app.route('/api/v1/categories', methods=[constants.REQUEST_GET])
def categories():
    try:
        access_token = request.headers.get('Authorization')
        response = splitwise.categories(access_token=access_token)
        return response
    except Exception as e:
        log.logger.error(e, exc_info=True)
        return constants.INTERNAL_ERROR_DICT, 500

@app.route('/api/v1/expenses', methods=[constants.REQUEST_POST])
def expenses():
    access_token = request.headers.get('Authorization')
    raw_expenses = request.json['expenses']
    try:
        created_expenses = splitwise.create_expense(raw_expenses=raw_expenses, access_token=access_token)
        return created_expenses, 200
    except Exception as e:
        log.logger.error(e, exc_info=True)
        return constants.INTERNAL_ERROR_DICT, 500 

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in app_config.FLASK_ALLOWED_EXTENSIONS

if __name__ == "__main__":
    app.run('0.0.0.0', 8080)
