import json
import datetime
import serial

class GnssData:

    time: str
    longitude: float
    latitude: float
    gnss_fix_ok: bool
    fix_type: int
    height: int
    v_acc: int
    h_acc: int
    num_sats_total: int
    num_sats_fixed: int
    msgUsed: int
    refStation: int
    satellites: list

    def __init__(self):
        pass

    def to_dict(self) -> dict:
        return {"time": self.time, "longitude": self.longitude, "latitude": self.latitude, "gnssFixOK": self.gnss_fix_ok, "fixType": self.fix_type, "height": self.height, "verticalAccuracy": self.v_acc, "horizontalAccuracy": self.h_acc, "numSatsTotal": self.num_sats_total, "numSatsFixed": self.num_sats_fixed, "msgUsed": self.msgUsed, "refStation": self.refStation, "satellites": self.satellites} 