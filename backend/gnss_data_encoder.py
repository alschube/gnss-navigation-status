# This project was developed in the context of my bachelor's thesis for the Applied Computer Science degree programme at Heilbronn University.
# It serves as a backend for my app to provide centimeter accurate positioning.
#
# The required data is received by the receiver and then passed on to the
# Raspberry Pi via the UART to be further processed in the backend and sent on to the frontend.
#
# It also serves as an interface for the frontend to communicate with the receiver to configure it.
#
# The Ublox Python package by SparkFun Electronics was used for the implementation.
# It was also extended by some additional functions, which where not provided by u-blox.
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

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
