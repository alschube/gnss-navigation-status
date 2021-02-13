import json
import datetime
import serial
from dataclasses import dataclass

@dataclass
class GnssData:

    time: str
    longitude: float
    latitude: float
    gnss_fix_ok: bool
    height: int
    v_acc: int
    h_acc: int
    #num_sats_total: int
    #num_sats_fixed: int
    #sat_id: int
    #sat_elev: float ?
    #sat_azim: float ?

    def __init__(self, time: datetime, longitude: float, latitude: float, gnss_fix_ok: int, height: int, v_acc: int, h_acc:int):
        self.time = str(time)
        self.longitude = longitude
        self.latitude = latitude
        self.gnss_fix_ok = bool(gnss_fix_ok)
        self.height = height
        self.v_acc = v_acc
        self.h_acc = h_acc

    def to_dict(self) -> dict:
        return{"Time": self.time, "Longitude": self.longitude, "Latitude": self.latitude, "GnssFixOK": self.gnss_fix_ok, "Height": self.height, "VerticalAccuracy": self.v_acc, "HorizontalAccuracy": self.h_acc} 