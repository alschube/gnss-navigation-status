# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import json
from json import JSONEncoder

class GnssDataEncoder(JSONEncoder):
    """
    Encodes gnss data to json
    
    :param JSONEncoder: the encoder
    """
    def default(self, o):
        """
        :param o: the object to encode
        :return: the converted object
        :rtype: converted dict
        """
        return o.__dict__