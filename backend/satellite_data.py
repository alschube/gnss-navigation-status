class SatelliteData:
    svId: int
    gnssId: int
    elevation: int
    azimut: int
    signal_strength: int

    def to_dict(self) -> dict:
        return {"svId": self.svId, "gnssId": self.gnssId, "elevation": self.elevation, "azimut": self.azimut, "signalStrength": self.signal_strength}
    
    def __init__(self):
        pass