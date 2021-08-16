# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

class SatelliteData:
    """
    this class is for creating satellite objects
    
    Attributes
    ----------
    svId : int
        the satellite identifier
    gnssId : int
        the gnss identifier
    elevation : int
        Elevation (range: +/- 90)
    azimut : int
        Azimuth (range 0-360)
    signal_strength : int
        Carrier to noise ratio (signal strength)
    """
    svId: int
    gnssId: int
    elevation: int
    azimut: int
    signal_strength: int

    def to_dict(self) -> dict:
        """
        converts this object to a dictionary that can be easily converted to a json string later
        """
        return {"svId": self.svId, "gnssId": self.gnssId, "elevation": self.elevation, "azimut": self.azimut, "signalStrength": self.signal_strength}