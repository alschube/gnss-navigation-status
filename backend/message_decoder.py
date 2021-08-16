# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import json
from message import Message
from types import SimpleNamespace

class MessageDecoder():
    """
    Decodes a message from json
    """
    
    def decodeFromJson(self, rawData):
        """
        decodes the given data from json to a message object
        
        :param rawData: the json string
        :return: the converted message
        :rtype: Message
        """
        msg = json.loads(rawData)
        return msg