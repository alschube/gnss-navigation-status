# This project was developed in the context of my bachelor thesis.
# It serves as a backend for my app to provide centimeter accurate positioning.
#
# The required data is received by the receiver and then passed on to the
# Raspberry Pi via the UART to be further processed in the backend and sent on to the frontend.
#
# It also serves as an interface for the frontend to communicate with the receiver to configure it.
#
# The Ublox Python package was used for the implementation.
# This package was also extended by some additional functions, which where not provided by u-blox
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

from ublox_gps.ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
from satellite_data import SatelliteData
import socket
import datetime
import multiprocessing
import serial
import json

class DataFetcher:
    """
    reads the ubx messages from the serial and sends them to the frontend via sockets
    
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
    gps : UbloxGps
        the hard port to communicate with the pHAT
    sock : socket
        the socket to open
    received_data : GnssData
        the received gnss data
    RTCM : multiprocessing.value (boolean)
        cross-process shared flag, indicates whether rtcm is enabled or not
    CONNECTION_ESTABLISHED : multiprocessing.value (boolean)
        cross-process shared flag, indicates whether the connection is established or not
    FINISH : Boolean
        indicates whether the processes should be terminated, is set from the parent process
    """
    
    # create a socket for TCP communication
    # first get the ip adress of itself
    # then open the socket
    ipdata = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ipdata.connect(('8.8.8.8', 80))
    TCP_IP = ipdata.getsockname()[0]
    TCP_PORT = 8765
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # create the serial port to communicate over UART
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(ser)
    
    # create empty variable for received gnss data and set some variables initially to zero
    received_data = GnssData()
    received_data.msgUsed = 0
    received_data.refStation = 0
    
    # create some process shared values
    # that can be read or edited from other processes
    RTCM = multiprocessing.Value('b', False)
    CONNECTION_ESTABLISHED = multiprocessing.Value('b', False)
    FINISH = False
        
    def setRTCM(self, status):
        """
        sets the value of RTCM
        
        :param status: the new value of RTCM
        """
        self.RTCM.value = status
        
    def setConnectionStatus(self, status):
        """
        set the connection status
        
        :param status: the new status of the connection
        """
        self.CONNECTION_ESTABLISHED.value = status
        
    def resetRTCMData(self):
        """
        resets the rtcm data
        """
        self.received_data.msgUsed = 0
        self.received_data.refStation = 0
        
    def createSocket(self):
        """
        opens the socket and binds it to the server_adress
        """
        server_address = ((self.TCP_IP, self.TCP_PORT))
        self.sock.bind(server_address)
        print('DataFetcher: starting up on %s port %s' % server_address)

        # Listen for incoming connections
        self.sock.listen(1)
        
    def connect(self):
        """
        connects to the frontend and sends the read gnss data
        runs until the socket is closed
        
        Raises
        -------
        AttributeError
            is raised if unexpected data was read
        ValueError
            is raised if the right type was received but the value is inappropriate
        IOError
            is raised if the given type could not be found
        """
        while True:
            if self.FINISH == True:
                self.sock.close()
                break
            # Wait for a connection
            print('DataFetcher: waiting for a connection')
            connection, client_address = self.sock.accept()
            try:
                print('DataFetcher: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    try:
                        # get the gnss data
                        self.get_geo_coords()
                        self.get_satellites()
                        if (self.RTCM.value == True and self.CONNECTION_ESTABLISHED.value == True):
                            print('DataFetcher: starting to poll rtcm data')
                            self.get_rtcm_status()
                        
                        #convert to json
                        gnssJSONData = json.dumps(self.received_data.to_dict(), indent=4, cls=GnssDataEncoder)
                        try:
                            # send the data
                            gnssJSONData = gnssJSONData.replace("\n", "")
                            connection.sendall((gnssJSONData + "\r\n").encode())
                        
                        # this will be executed if the client has closed the connection
                        except Exception as err:
                            print("DataFetcher: client", client_address," closed the connection, no more data can be sent")
                            break
                        
                    except(AttributeError) as err:
                        print("DataFetcher: no expected data found, trying again.....")
                        print(err)
                        continue

                    except (ValueError, IOError) as err:
                        print(err)
                        continue
                        
            finally:
                # Clean up the connection
                connection.close()
                
        self.sock.close()
            
    def run(self):
        """
        This starts the connection
        """
        self.createSocket()
        self.connect()
        
    def get_geo_coords(self):
        """
        fetches the geo coordinates from the serial
        and assings it to the gnss data object
        """
        raw_data = self.gps.geo_coords()
        self.received_data.time = str(datetime.datetime.now())
        self.received_data.longitude = raw_data.lon
        self.received_data.latitude = raw_data.lat
        self.received_data.gnss_fix_ok = raw_data.flags.gnssFixOK
        self.received_data.fix_type = raw_data.fixType
        self.received_data.height = raw_data.hMSL
        self.received_data.v_acc = raw_data.vAcc
        self.received_data.h_acc = raw_data.hAcc
        self.received_data.num_sats_fixed = raw_data.numSV
        #print(raw_data)
        
    def get_satellites(self):
        """
        fetches the satellites from the serial
        and assigns each of them to the gnss data object with its own attributes
        """
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
        
    def get_rtcm_status(self):
        """
        fetches the rtcm data from the serial
        """
        raw_data = self.gps.rtcm_status()
        self.received_data.msgUsed = raw_data.flags.msgUsed
        self.received_data.refStation = raw_data.refStation

if __name__ == '__main__':
    data_fetcher = DataFetcher()
    data_fetcher.run()