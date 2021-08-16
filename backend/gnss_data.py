# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import json
import datetime
import serial

class GnssData:
    """
    This class is for creating gnss data class objects
    
    Attributes
    ----------
    time : str
        the current UTC timestamp
    longitude : float
        the current longitude
    latitude : float
        the current latitude
    gnss_fix_ok : boolean
        flag that indicates if a gnss fix could be established
    fix_type : int
        the gnss fix type
    height : int
        height above mean sea level
    v_acc : int
        vertical accuracy estimate
    h_acc : int
        horizontal accuracy estimate
    num_sats_total : int
        total number of visible satellites
    num_sats_fixed : int
        number of sats used in Nav Solution
    msgUsed : int
        2 if rtcm is used successfully by the receiver
        1 if not used
        0 if unknown
    refStation : int
        reference station ID of the received RTCM input message, valid range 0 - 4095
    satellites : list
        a list of all tracked satellites
    """

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

    def to_dict(self) -> dict:
        """
        converts the object to a dictionary that can be converted to json
        
        :return: the dict
        :rtype: dict
        """
        return {"time": self.time, "longitude": self.longitude, "latitude": self.latitude, "gnssFixOK": self.gnss_fix_ok, "fixType": self.fix_type, "height": self.height, "verticalAccuracy": self.v_acc, "horizontalAccuracy": self.h_acc, "numSatsTotal": self.num_sats_total, "numSatsFixed": self.num_sats_fixed, "msgUsed": self.msgUsed, "refStation": self.refStation, "satellites": self.satellites} 