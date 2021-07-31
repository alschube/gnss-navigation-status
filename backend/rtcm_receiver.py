import socket
import serial
import multiprocessing

class RtcmReceiver:
    ipdata = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ipdata.connect(('8.8.8.8', 80))
    TCP_IP = ipdata.getsockname()[0]
    TCP_PORT = 8766 # Port to listen
    BUFFER_SIZE = 20
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating over UART1
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
    
    rtcmEnabled = False    
    
    def __init__(self):
        pass
    
    def createSocket(self):
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('RtcmReceiver: starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        t = multiprocessing.current_process()
        while True:
            # Wait for a connection
            print('RtcmReceiver: waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('RtcmReceiver: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while getattr(t, "do_run", True):
                    #connection.sendall(self.ser.readline())
                    data = connection.recv(16)
                    print('RtcmReceiver: received "%s"' % data)
                    if data:
                        self.ser.write(data)
                    else:
                        print('RtcmReceiver: no more data from', client_address)
                        break
                    
            finally:
                # Clean up the connection
                connection.close()
                self.sock.close()
            
    def run(self):
       self.createSocket()
       self.connect()

if __name__ == '__main__':
    rtcm_receiver = RtcmReceiver()
    rtcm_receiver.run()