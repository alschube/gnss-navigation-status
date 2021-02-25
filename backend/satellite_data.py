class SatelliteData:
    id: int
    elevation: int
    azimut: int
    signal_strength: int

    def to_dict(self) -> dict:
        return {"id": self.id, "elevation": self.elevation, "azimut": self.azimut, "signalStrength": self.signal_strength}
    
    def __init__(self):
        pass