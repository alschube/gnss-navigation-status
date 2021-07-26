import socket
import serial
import threading

class PositionTransmitter:
    TCP_IP = '192.168.178.44'# local host
    TCP_PORT = 8768 # Port to listen
    BUFFER_SIZE = 20
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating over UART1
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
      
    
    def __init__(self):
        pass 

    def createSocket(self):
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('PositionTransmitter: starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        t = threading.currentThread()
        while True:
            # Wait for a connection
            print('PositionTransmitter: waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('PositionTransmitter: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while getattr(t, "do_run", True):
                    connection.sendall(self.ser.readline())
                    
            except (BrokenPipeError) as err:
                print('Sender closed the connection ', err)
            except (ConnectionResetError) as err:
                print('Sender reset the connection ', err)
            finally:
                # Clean up the connection
                connection.close()
            
    def run(self):
       self.createSocket()
       self.connect()

if __name__ == '__main__':
    position_transmitter = PositionTransmitter()
    position_transmitter.run()