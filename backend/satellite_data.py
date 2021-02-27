class SatelliteData:
    satId: int
    gnssId: int
    elevation: int
    azimut: int
    signal_strength: int

    def to_dict(self) -> dict:
        return {"satId": self.satId, "gnssId": self.gnssId, "elevation": self.elevation, "azimut": self.azimut, "signalStrength": self.signal_strength}
    
    def __init__(self):
        pass