# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import socket
import serial
import sys

class RtcmForwarder:
    """
    forwards rtcm data to the receiver over uart
    
    Attributes
    ----------
    ipdata : socket
        helper socket to get your own ip address
    TCP_IP : str
        the receivers ip address
    TCP_PORT : int
        the port to open
    ser : int
        the serial to communicate over
    sock : socket
        the socket to open
    rtcmEnabled : Boolean
        indicates if the rtcm messages should be enabled or not
    connectionEstablished : Boolean
        indicates whether a connection to the ntrip client is existing or not
    FINISH : Boolean
        indicates whether the processes should be terminated, is set from the parent process
    dataFetcher : None
        a data fetcher instance
    """
    ipdata = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ipdata.connect(('8.8.8.8', 80))
    TCP_IP = ipdata.getsockname()[0]
    TCP_PORT = 8766 # Port to listen
    
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1) # Create a serial for communicating over UART1
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Create a TCP/IP socket
    
    rtcmEnabled = None
    connectionEstablished = None
    FINISH = False
    dataFetcher = None
    
    def __init__(self):
        """
        inits rtcmEnabled with False
        """
        self.rtcmEnabled = False
        pass
    
    def setRtcmEnabled(self, bool):
        """
        sets rtcmEnabled to the given parameter
        
        :param bool: the new value of rtcmEnabled
        """
        self.rtcmEnabled = bool
        print('RtcmForwarder: set rtcmEnabled to ', bool)
        print('RtcmForwarder: sending data over uart to receiver')
        
    def setFetcherInst(self, fetcher):
        """
        sets the given object as the new value of data fetcher instance
        
        :param fetcher: the new fetcher
        """
        self.dataFetcher = fetcher
        print('MsgFetcher', self.dataFetcher)
        
    def createSocket(self):
        """
        opens the socket and binds it to the server_adress
        """
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('RtcmForwarder: starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        """
        connects to the ntrip client and receives rtcm data stream
        then write it on the serial (if rtcm is enabled)
        
        if the FINISH flag gets true, then terminate this process and its subprocess
        """
        while True:
            if self.FINISH == True:
                self.sock.close()
                break
            # Wait for a connection
            print('RtcmForwarder: waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('RtcmForwarder: connection from', client_address)
                self.dataFetcher.setConnectionStatus(True)
                # Receive the data in small chunks and retransmit it
                while True:
                    data = connection.recv(16)
                    if data:
                        if self.rtcmEnabled:
                            self.ser.write(data)
                    else:
                        print('RtcmForwarder: no more data from', client_address)
                        break
            
            finally:
                # Clean up the connection
                connection.close()
                self.dataFetcher.setConnectionStatus(False)
                
        self.sock.close()
            
    def run(self):
        """
        this runs the rtcm forwarder
        """
        self.createSocket()
        self.connect()

if __name__ == '__main__':
    rtcm_forwarder = RtcmForwarder()
    rtcm_forwarder.run()