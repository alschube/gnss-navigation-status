from ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
from satellite_data import SatelliteData        
import socket
import datetime
import serial
import json

class DataFetcher:
    TCP_IP = '192.168.178.44'# local host
    TCP_PORT = 8765 # Port to listen
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
    
    gps = UbloxGps(ser)
    received_data = GnssData()
    
    def __init__(self):
        pass

    def createSocket(self):
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        while True:
            # Wait for a connection
            print('waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    try:
                        self.get_geo_coords()
                        self.get_satellites()
                        gnssJSONData = json.dumps(self.received_data.to_dict(), indent=4, cls=GnssDataEncoder)
                        
                        try:
                            connection.sendall(str(gnssJSONData + "\r\n").encode())
                            print("Successfully send data to client", client_address)
                            
                        except Exception as err:
                            print(err)
                            print("client", client_address," closed the connection, no more data can be sent")
                            break

                        #print(gnssJSONData)
                        
                    except(AttributeError) as err:
                        print(err)
                        print("no data found, trying again.....")
                        continue

                    except (ValueError, IOError) as err:
                        print(err)
                        
            finally:
                # Clean up the connection
                connection.close()
            
    def run(self):
        self.createSocket()
        self.connect()
        
    def get_geo_coords(self):
        raw_data = self.gps.geo_coords()
        self.received_data.time = str(datetime.datetime.now())
        self.received_data.longitude = raw_data.lon
        self.received_data.latitude = raw_data.lat
        self.received_data.gnss_fix_ok = raw_data.flags.gnssFixOK
        self.received_data.height = raw_data.hMSL
        self.received_data.v_acc = raw_data.vAcc
        self.received_data.h_acc = raw_data.hAcc
        self.received_data.num_sats_fixed = raw_data.numSV
        
    def get_satellites(self):
        raw_data = self.gps.satellites()
        self.received_data.num_sats_total = raw_data.numSvs
        temp = raw_data.RB
        satellites = []
        for sat in temp:
            satellite_data = SatelliteData()
            satellite_data.svId = sat.svId
            satellite_data.gnssId = sat.gnssId
            satellite_data.elevation = sat.elev
            satellite_data.azimut = sat.azim
            satellite_data.signal_strength = sat.cno
            satellites.append(satellite_data.to_dict())
        self.received_data.satellites = satellites

if __name__ == '__main__':
    data_fetcher = DataFetcher()
    data_fetcher.run()