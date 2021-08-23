# This project was developed in the context of my bachelor thesis.
# It serves as a backend for my app to provide centimeter accurate positioning.
#
# The required data is received by the receiver and then passed on to the
# Raspberry Pi via the UART to be further processed in the backend and sent on to the frontend.
#
# It also serves as an interface for the frontend to communicate with the receiver to configure it.
#
# The Ublox Python package was used for the implementation.
# This package was also extended by some additional functions, which where not provided by u-blox
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