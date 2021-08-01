import json
import datetime
import serial

class GnssData:

    time: str
    longitude: float
    latitude: float
    gnss_fix_ok: bool
    height: int
    v_acc: int
    h_acc: int
    num_sats_total: int
    num_sats_fixed: int
    satellites: list

    def __init__(self):
        pass

    def to_dict(self) -> dict:
        return {"time": self.time, "longitude": self.longitude, "latitude": self.latitude, "gnssFixOK": self.gnss_fix_ok, "height": self.height, "verticalAccuracy": self.v_acc, "horizontalAccuracy": self.h_acc, "numSatsTotal": self.num_sats_total, "numSatsFixed": self.num_sats_fixed, "satellites": self.satellites} 