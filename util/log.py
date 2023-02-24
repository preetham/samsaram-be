import logging

logger = logging.getLogger()

_handler = logging.StreamHandler()
_handler.setLevel(logging.DEBUG)
_handler.setFormatter(logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s'))

logger.addHandler(_handler)