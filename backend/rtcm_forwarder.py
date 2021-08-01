import socket
import serial
import sys

class RtcmForwarder:
    ipdata = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ipdata.connect(('8.8.8.8', 80))
    TCP_IP = ipdata.getsockname()[0]
    TCP_PORT = 8766 # Port to listen
    BUFFER_SIZE = 20
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating over UART1
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
    
    rtcmEnabled = None
    connectionEstablished = None
    FINISH = False
    
    def __init__(self):
        self.rtcmEnabled = False
        pass
    
    def setRtcmEnabled(self, bool):
        self.rtcmEnabled = bool
        print('RtcmForwarder: set rtcmEnabled to ', bool)
        print('RtcmForwarder: sending data over uart to receiver')
        
    def createSocket(self):
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('RtcmForwarder: starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        while True:
            if self.FINISH == True:
                self.sock.close()
                break
            # Wait for a connection
            print('RtcmForwarder: waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('RtcmForwarder: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    data = connection.recv(16)
                    #print('RtcmForwarder: received "%s"' % data)
                    if data:
                        if self.rtcmEnabled:
                            self.ser.write(data)
                    else:
                        print('RtcmForwarder: no more data from', client_address)
                        break
            
            finally:
                # Clean up the connection
                connection.close()
                
        self.sock.close()
            
    def run(self):
       self.createSocket()
       self.connect()

if __name__ == '__main__':
    rtcm_forwarder = RtcmForwarder()
    rtcm_forwarder.run()