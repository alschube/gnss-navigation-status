# This script is a part of the backend for my bachelor thesis
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