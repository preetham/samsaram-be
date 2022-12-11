import gunicorn.app.base
import multiprocessing

from app import app
from config.settings import app_config

def number_of_workers():
    return 1

class SamsaramApp(gunicorn.app.base.BaseApplication):
    def __init__(self, app, options=None):
        self.options = options or {}
        self.application = app
        super(SamsaramApp, self).__init__()
    
    def load_config(self):
        config = {key: value for key, value in self.options.items()
                  if key in self.cfg.settings and value is not None}
        for key, value in config.items():
            self.cfg.set(key.lower(), value)

    def load(self):
        return self.application

if __name__ == '__main__':
    options = {
        'bind': '%s:%s' % (app_config.APP_HOST, app_config.APP_PORT),
        'workers': number_of_workers(),
    }
    SamsaramApp(app, options).run()