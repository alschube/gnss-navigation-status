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
from message_decoder import MessageDecoder
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder

class MessageHandler:
    HOST = '192.168.178.44'  # Standard loopback interface address (localhost)
    PORT = 8764     # Port to listen on (non-privileged ports are > 1023)
    ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(ser)
    
    # Create a TCP/IP socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    message_decoder = MessageDecoder()
    gnss_configurator = GnssConfigurator()
    rtcm_forwarder = RtcmForwarder()
    reply_message = Message()
    isRtcmEnabled = None
    
    def __init__(self):
        pass
    
    def open_socket_connection(self):
        server_address = ((self.HOST, self.PORT))
        self.s.bind(server_address)
        print('MessageHandler: starting up on %s port %s' % server_address)
        # Listen for incoming connections
        self.s.listen(1)
       
    def check_msg_type(self, msg):
        msg_type = msg["msgType"]
        msg_content = msg["msgContent"]
        payload_message = None
        if(msg_type=="GNSS_GET"):
            print('MessageHandler: Fetching satellite config.....')
            payload_message = self.gnss_configurator.getSatelliteConfiguration()
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
                
        elif(msg_type=="RTCM_CONFIG"):
            print("got rtcm config message")
            if msg_content=="enable rtcm":
                self.isRtcmEnabled = True
            elif msg_content=="disable rtcm":
                self.isRtcmEnabled = False
            self.rtcm_forwarder.setRtcmEnabled(self.isRtcmEnabled)
            payload_message = "RTCM ACK"
            
        
        #print("Message Payload :", payload_message)
        return payload_message
    
    def check_payload_message(self, payload_message):
        if(len(payload_message) > 3):
            return 
        if(payload_message == "ACK"):
            return True
        else:
            return False
    
    def connect(self):
        while True:
            # Wait for a connection
            print('MessageHandler: waiting for a connection')
            connection, client_address = self.s.accept()
            try:
                print('MessageHandler: connection from', client_address)
                # Receive the data in small chunks and retransmit it
                while True:
                    data = str(connection.recv(2048))
                    data = data[2:]
                    data = data[:-3]
                    print('received "%s"' % data)
                    if data:
                        #print(data)
                        msg = self.message_decoder.decodeFromJson(data)
                        #print(msg["msgType"])
                        #print(msg["msgContent"])
                        
                        msg_payload = self.check_msg_type(msg)
                        
                        #print(msg_payload)
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
                        #messageJSONData = self.reply_message.encodeToJson()
                        #messageJSONData = json.dumps(self.reply_message.to_dict(), indent=4, cls=MessageEncoder)
                        messageJSONData = json.JSONEncoder(sort_keys=True, indent=4).encode(self.reply_message.to_dict())
                        print("messageJSONData :", str(messageJSONData))
                        connection.sendall(str(messageJSONData.replace("\n", "") + "\r\n").encode())
                            
                        #connection.sendall("i received something from you\r\n".encode())
                    else:
                        print('MessageHandler: no more data from', client_address)
                        break
                    
            finally:
                # Clean up the connection
                connection.close()
            
    def runRtcmForwarder(self):
        #running Rtcm Forwarder on new Thread
        thread1 = threading.Thread(target=self.rtcm_forwarder.run)
        thread1.start()

    def run(self):
        self.runRtcmForwarder()
        self.open_socket_connection()
        self.connect()
        


if __name__ == '__main__':
    message_handler = MessageHandler()
    message_handler.run()

