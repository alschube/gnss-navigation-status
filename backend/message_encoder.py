import json
from json import JSONEncoder

class MessageEncoder(JSONEncoder):
    def default(self, o):
        return o.__dict__