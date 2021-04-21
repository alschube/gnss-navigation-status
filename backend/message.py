import json
from enum import Enum
from message_encoder import MessageEncoder

class Message():
    
    class Type(Enum):
        GNSS_DATA = 'GNSS_DATA'
        RTCM_CONFIG = 'RTCM_CONFIG'
        GNSS_CONFIG = 'GNSS_CONFIG'
    
    msg_type: Type
    msg_content: str
    
    def __init__(self):
        pass
    
    def to_dict(self) -> dict:
        return {"type":self.msg_type.name, "content":self.msg_content}
    
    def encodeToJson(self):
        return json.dumps(self.to_dict(), indent=4, cls=MessageEncoder)