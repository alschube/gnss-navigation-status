import json
from message import Message
from types import SimpleNamespace

class MessageDecoder():
    
    def decodeFromJson(self, rawData):
        msg = json.loads(rawData)
        return msg