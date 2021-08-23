# This project was developed in the context of my bachelor's thesis for the Applied Computer Science degree programme at Heilbronn University.
# It serves as a backend for my app to provide centimeter accurate positioning.
#
# The required data is received by the receiver and then passed on to the
# Raspberry Pi via the UART to be further processed in the backend and sent on to the frontend.
#
# It also serves as an interface for the frontend to communicate with the receiver to configure it.
#
# The Ublox Python package by SparkFun Electronics was used for the implementation.
# It was also extended by some additional functions, which where not provided by u-blox.
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import socket
import serial
import json
import datetime
import multiprocessing
import threading
import sys
from ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
from satellite_data import SatelliteData
from message import Message
from message_encoder import MessageEncoder
from message_decoder import MessageDecoder
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder

class MessageHandler:
    """
    handles incoming messages
    
    Attributes
    ----------
    ipdata : socket
        helper socket to get your own ip address
    HOST : str
        the receivers ip address
    PORT : int
        the port to open
    ser : int
        the serial to communicate over
    gps : UbloxGps
        the hard port to communicate with the pHAT
    s : socket
        the socket to open
    message_decoder : MessageDecoder
        a message decoder instance
    gnss_configurator : GnssConfigurator
        a gnss configurator instance
    rtcm_forwarder : RtcmForwarder
        a rtcm forwarder instance
    dataFetcher : None
        a data fetcher instance
    reply_message : Message
        the reply to the incoming message
    isRtcmEnabled : Boolean
        indicates if the rtcm messages should be enabled or not
    FINISH : Boolean
        indicates whether the processes should be terminated, is set from the parent process
    """
    
    # create a socket for TCP communication
    # first get the ip adress of itself
    # then open the socket
    ipdata = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ipdata.connect(('8.8.8.8', 80))
    HOST = ipdata.getsockname()[0]
    PORT = 8764     # Port to listen on (non-privileged ports are > 1023)
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(ser)
    
    # Create a TCP/IP socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    message_decoder = MessageDecoder()
    gnss_configurator = GnssConfigurator()
    rtcm_forwarder = RtcmForwarder()
    dataFetcher = None
    reply_message = Message()
    
    isRtcmEnabled = False
    FINISH = False

    def open_socket_connection(self):
        """
        opens the socket and binds it to the server_adress
        """
        server_address = ((self.HOST, self.PORT))
        self.s.bind(server_address)
        print('MessageHandler: starting up on %s port %s' % server_address)
        # Listen for incoming connections
        self.s.listen(1)
       
    def check_msg_type(self, msg):
        """
        checks which type of message is received from frontend
        and send a message to the receiver afterwards or sets some variables
        
        :param msg: the received message
        :return: the payload of the message that came back from the receiver
        :rtype: str
        """
        msg_type = msg["msgType"]
        msg_content = msg["msgContent"]
        payload_message = None
        
        # if type GNSS_GET then get the gnss config
        if(msg_type=="GNSS_GET"):
            print('MessageHandler: Fetching satellite config.....')
            payload_message = self.gnss_configurator.getSatelliteConfiguration()
        
        # if type GNSS_CONFIG then set the gnss config
        elif(msg_type=="GNSS_CONFIG"):
            if(msg_content=="enable GPS"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.enable_GPS))
            elif(msg_content=="disable GPS"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.disable_GPS))
            elif(msg_content=="enable BDS"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.enable_BDS))
            elif(msg_content=="disable BDS"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.disable_BDS))
            elif(msg_content=="enable GLO"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.enable_GLO))
            elif(msg_content=="disable GLO"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.disable_GLO))
            elif(msg_content=="enable GAL"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.enable_GAL))
            elif(msg_content=="disable GAL"):
                payload_message = self.gnss_configurator.setSatelliteConfiguration(self.gnss_configurator.hexToBytes(self.gnss_configurator.disable_GAL))
            
        # if type RTCM-CONFIG then set rtcm to the given value
        elif(msg_type=="RTCM_CONFIG"):
            print("got rtcm config message")
            if msg_content=="enable rtcm":
                self.isRtcmEnabled = True
            elif msg_content=="disable rtcm":
                self.isRtcmEnabled = False
            self.rtcm_forwarder.setRtcmEnabled(self.isRtcmEnabled)
            self.dataFetcher.setRTCM(self.isRtcmEnabled)
            self.dataFetcher.resetRTCMData()
            payload_message = "RTCM ACK"
            
        return payload_message
    
    def check_payload_message(self, payload_message):
        """
        checks whether an ACK was returned or something longer
        
        :param payload_message: the payload to check
        :return: True if ACK
        :rtype: bool
        """
        if(len(payload_message) > 3):
            return 
        if(payload_message == "ACK"):
            return True
        else:
            return False
    
    def connect(self):
        """
        connects to the frontend and waits for incoming messages
        if a message is received it will be parsed and handled accordingly
        then sends a reply corresponding to the determined type
        
        if the FINISH flag gets true, then terminate this process and its subprocess
        """
        while True:
            if self.FINISH == True:
                rtcm_forwarder.FINISH = True
                self.s.close()
                break
            # Wait for a connection
            print('MessageHandler: waiting for a connection')
            connection, client_address = self.s.accept()
            try:
                print('MessageHandler: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    data = str(connection.recv(2048))
                    #strip the data
                    data = data[2:]
                    data = data[:-3]
                    if data:
                        print('received "%s"' % data)
                        
                        #decode the message and check its type
                        msg = self.message_decoder.decodeFromJson(data)
                        msg_payload = self.check_msg_type(msg)
                        
                        #send a reply according to the type
                        if (type(msg_payload) is not str):
                            self.reply_message.msg_type = self.reply_message.Type.GNSS_GET
                            sat_data_dict = {"GPS":msg_payload[0], "GLONASS":msg_payload[1], "BeiDou":msg_payload[2], "Galileo":msg_payload[3]}
                            self.reply_message.msg_content = str(sat_data_dict)
                        elif("RTCM" in msg_payload):
                            self.reply_message.msg_type = self.reply_message.Type.RTCM_CONFIG
                            self.reply_message.msg_content = msg_payload
                        else:
                            self.reply_message.msg_type = self.reply_message.Type.GNSS_CONFIG
                            self.reply_message.msg_content = msg_payload
                        
                        messageJSONData = json.JSONEncoder(sort_keys=True, indent=4).encode(self.reply_message.to_dict())
                        connection.sendall(str(messageJSONData.replace("\n", "") + "\r\n").encode())

                    else:
                        print('MessageHandler: no more data from', client_address)
                        break
            
            finally:
                # Clean up the connection
                connection.close()
                
        self.s.close()
            
    def runRtcmForwarder(self):
        """
        starts a new subprocess for the rtcm forwarder
        """
        thread = threading.Thread(target=self.rtcm_forwarder.run)
        thread.start()
        
        self.rtcm_forwarder.setFetcherInst(self.dataFetcher)

    def setMsgHandlerInst(self, fetcher):
        """
        sets the data fetcher instance to the given one
        
        :param fetcher: the object to set the instance to
        """
        self.dataFetcher = fetcher
        #print('MsgFetcher', self.dataFetcher)

    def run(self):
        """
        this runs the rtcm forwarder
        and then starts the connection
        """
        self.runRtcmForwarder()
        self.open_socket_connection()
        self.connect()


if __name__ == '__main__':
    message_handler = MessageHandler()
    message_handler.run()

