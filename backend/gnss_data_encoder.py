import json
from json import JSONEncoder

class GnssDataEncoder(JSONEncoder):
    def default(self, o):
        return o.__dict__