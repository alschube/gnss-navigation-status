# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import json
from json import JSONEncoder

class MessageEncoder(JSONEncoder):
    """
    Encodes a message to a dictionary that can later be easily converted to a json string
    """
    def default(self, o):
        """
        encodes the given object to a dictionary
        
        :param o: the object to convert
        :return: the dict
        :rtype: dict
        """
        return o.__dict__