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

class MessageHandler:
    HOST = '192.168.178.44'  # Standard loopback interface address (localhost)
    PORT = 8764     # Port to listen on (non-privileged ports are > 1023)
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(ser)
    
    # Create a TCP/IP socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    
    def __init__(self):
        pass
    
    def open_socket_connection(self):
        server_address = ((self.HOST, self.PORT))
        self.s.bind(server_address)
        print('starting up on %s port %s' % server_address)
        # Listen for incoming connections
        self.s.listen(1)
        
    def connect(self):
        while True:
            # Wait for a connection
            print('waiting for a connection')
            connection, client_address = self.s.accept()
            try:
                print('connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    data = str(connection.recv(2048))
                    data = data[2:]
                    data = data[:-3]
                    print('received "%s"' % data)
                    if data:
                        print(len(data))
                        connection.sendall("i received something from you\r\n".encode())
                    else:
                        print('no more data from', client_address)
                        break
                    
            finally:
                # Clean up the connection
                connection.close()
            
    def run(self):
        self.open_socket_connection()
        self.connect()


if __name__ == '__main__':
    message_handler = MessageHandler()
    message_handler.run()

