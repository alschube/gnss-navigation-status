import socket
import serial

class RtcmForwarder:
    TCP_IP = '192.168.178.44'# local host
    TCP_PORT = 8766 # Port to listen
    BUFFER_SIZE = 20
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating over UART1
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
    
    
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
                    data = connection.recv(16)
                    print('received "%s"' % data)
                    if data:
                        print('sending data over uart to receiver')
                        self.ser.write(data)
                    else:
                        print('no more data from', client_address)
                        break
                    
            finally:
                # Clean up the connection
                connection.close()
            
    def run(self):
       self.createSocket()
       self.connect()

if __name__ == '__main__':
    rtcm_forwarder = RtcmForwarder()
    rtcm_forwarder.run()