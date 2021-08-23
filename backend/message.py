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

import json
from enum import Enum
from message_encoder import MessageEncoder

class Message():
    """
    this class creates message objects with a specific type and content
    
    Attributes
    ----------
    Type : Enum
        all types a message can have
    msg_type : Type
        the actual type of the message
    msg_content : str
        the content of the message
    """
    
    class Type(Enum):
        RTCM_CONFIG = 'RTCM_CONFIG'
        GNSS_CONFIG = 'GNSS_CONFIG'
        GNSS_GET = 'GNSS_GET'
    
    msg_type: Type
    msg_content: str
    
    def to_dict(self) -> dict:
        """
        converts the object to a dictionary that can be converted to json
        
        :return: the dict
        :rtype: dict
        """
        return {"type":self.msg_type.name, "content":self.msg_content}
    
    def encodeToJson(self):
        """
        Encodes message to json
    
        :return: the converted object
        :rtype: json string
        """
        return json.dumps(self.to_dict(), indent=4, cls=MessageEncoder)
