import socket
import serial
import json
import datetime
import threading
from ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
from satellite_data import SatelliteData
from message import Message
from message_encoder import MessageEncoder

class SocketServer:
    HOST = '192.168.178.44'  # Standard loopback interface address (localhost)
    PORT = 8766     # Port to listen on (non-privileged ports are > 1023)
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(ser)
    receiver_data = GnssData()
    
    # Create a TCP/IP socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = ((HOST, PORT))
    
    def __init__(self):
        pass
    
    def open_socket_connection(self):
        self.s.bind(self.server_address)
        print('starting up on %s port %s' % self.server_address)
        # Listen for incoming connections
        self.s.listen(1)
        
    def receive_message(self, connection, client_address):
        data = str(connection.recv(2048))
        print('received "%s"' % data)
        data = data[2:]
        data = data[:-3]
        if data:
            #print('got something')
            print(data)
            #msg = json.loads(data)
            #print(msg["msgContent"])
            connection.sendall("i received something from you\r\n".encode())
            #ser.write(data)
        else:
            print('no more data from', client_address)
                
    def get_geo_coords(self):
        raw_data = self.gps.geo_coords()
        self.receiver_data.time = str(datetime.datetime.now())
        self.receiver_data.longitude = raw_data.lon
        self.receiver_data.latitude = raw_data.lat
        self.receiver_data.gnss_fix_ok = raw_data.flags.gnssFixOK
        self.receiver_data.height = raw_data.hMSL
        self.receiver_data.v_acc = raw_data.vAcc
        self.receiver_data.h_acc = raw_data.hAcc
        self.receiver_data.num_sats_fixed = raw_data.numSV
        
    def get_satellites(self):
        raw_data = self.gps.satellites()
        self.receiver_data.num_sats_total = raw_data.numSvs
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
        
        self.receiver_data.satellites = satellites
        
    def send_gnss_data(self, connection):
        #gnssJSONData = json.dumps(self.receiver_data.to_dict(), indent=4, cls=GnssDataEncoder)
        #print(gnssJSONData)
        message = Message()
        message.msg_type = Message.Type.GNSS_DATA
        message.msg_content = str(self.receiver_data.to_dict())
        #print(message)
        messageJSONData = json.dumps(message.to_dict(), indent=4, cls=MessageEncoder)
        messageJSONData = message.encodeToJson()
        print("_________")
        print(messageJSONData)
        print("_________")
        connection.sendall(str(messageJSONData + "\r\n").encode())
        #connection.sendall(str(gnssJSONData + "\r\n").encode())
        self.receiver_data = GnssData()
        
    def get_and_send_data(self, connection):
        try:
            self.get_geo_coords()
            self.get_satellites()
            self.send_gnss_data(connection)
        except(AttributeError) as err:
            print(err.with_traceback)
            print("no data found")
            #continue
        except(ValueError, IOError) as err:
           print(err)
        
    def run(self):
        self.open_socket_connection()
        # Wait for a connection
        print('waiting for a connection')
        connection, client_address = self.s.accept()
        #thread1 = threading.Thread(target=self.get_and_send_data, args=(connection))
        #thread2 = threading.Thread(target=self.receive_message, args=(connection, client_address))
        try:
            print('connection from', client_address)
            # Receive the data in small chunks and retransmit it
            while True:
                self.get_and_send_data(connection)
                self.receive_message(connection, client_address)
            #thread1.start()
            #thread2.start()
        finally:
            # Clean up the connection
            connection.close()
        
if __name__ == '__main__':
    socket_server = SocketServer()
    socket_server.run()
    