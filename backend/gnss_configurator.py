# This script is a part of the backend for my bachelor thesis
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import serial
from ublox_gps import sparkfun_predefines as sp
from gnss_data_fetcher import DataFetcher
from ublox_gps import core
from ublox_gps import UbloxGps

class GnssConfigurator:
    """
    This class converts a hex string into bytes and sends it over uart to the pHAT
    for configuring the gnss
    
    Attributes
    ----------
    port : serial
        the serial to communicate over with the receiver
    gps : UbloxGps
        the hard port to communicate with the pHAT
    rec_msg : None
        the reply message to send
    enable_GPS : str
        the payload to send to enable GPS
    disable_GPS : str
        the payload to send to disable GPS
    enable_GAL : str
        the payload to send to enable Galileo
    disable_GAL : str
        the payload to send to disable Galileo
    enable_GLO : str
        the payload to send to enable Glonass
    disable_GLO : str
        the payload to send to disable Glonass
    enable_BDS : str
        the payload to send to enable Beidou
    disable_BDS : str
        the payload to send to disable Beidou
    """
    
    # Create a serial for communication
    port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
    gps = UbloxGps(port)
    
    # the reply message
    rec_msg = None

    # GPS
    enable_GPS='00 01 00 00 1f 00 31 10 01 fb 80'
    disable_GPS='00 01 00 00 1f 00 31 10 00 fa 7f'

    # GALILEO
    enable_GAL='00 01 00 00 21 00 31 10 01 fd 8a'
    disable_GAL='00 01 00 00 21 00 31 10 00 fc 89'

    # GLONASS
    enable_GLO='00 01 00 00 25 00 31 10 01 01 9e'
    disable_GLO='00 01 00 00 25 00 31 10 00 00 9d'

    # BEIDOU
    enable_BDS='00 01 00 00 22 00 31 10 01 fe 8f'
    disable_BDS='00 01 00 00 22 00 31 10 00 fd 8e'

    def hexToBytes(self, byteString):
        """
        This method converts a hex string into bytes
        
        :param byteString:   the string to convert into bytes
        
        :return: the converted string
        :rtype: bytes
        """
        
        hex_in_bytes = bytes.fromhex(byteString)
        print('Converted to: ', hex_in_bytes)
        return hex_in_bytes

    def getSatelliteConfiguration(self):
        """
        Sends a poll request for the MON class with the GNSS Message ID and
        parses ublox messages for the response. The payload is extracted from
        the response which is then passed to the user.
        
        The payload contains information about the current gnss configuration

        :return: The payload of the MON Class and GNSS Message ID
        :rtype: namedtuple
        
        Raises
        ------
        AttributeError
            is raised if unexpected data was read
        ValueError
            is raised if the right type was received but the value is inappropriate
        IOError
            is raised if the given type could not be found
        """
        
        while True:
            try:
                self.gps.send_message(sp.MON_CLS, self.gps.mon_ms.get('GNSS'))
                parse_tool = core.Parser([sp.MON_CLS, sp.ACK_CLS])
                cls_name, message, payload = parse_tool.receive_from(self.gps.hard_port)
                if cls_name == 'ACK':
                    print('Wrong message received, trying again...')
                    raise AttributeError()
                
                print('Payload:', payload.enabled)
                self.rec_msg = payload.enabled
                if self.rec_msg != None:
                    return self.rec_msg
            except (ValueError, IOError) as err:
                #print('An error occured: ' ,err)
                #print('Trying again.....')
                continue
            except (AttributeError) as err:
                continue
            
            
    def setSatelliteConfiguration(self, bytesPayload):
        """
        Sends a ublox message for the CFG class with the VALSET Message ID to the ublox module.
        The payload is extracted from the response which is then passed to the user.

        :param bytesPayload: the byte string that is send
        :return: ACK on completion or NAK on failure
        :rtype: string
        
        Raises
        ------
        ValueError
            is raised if the right type was received but the value is inappropriate
        IOError
            is raised if the given type could not be found
        """
        
        while True:
            try:
                self.gps.send_message(sp.CFG_CLS, self.gps.cfg_ms.get('VALSET'), bytesPayload)
                parse_tool = core.Parser([sp.CFG_CLS, sp.ACK_CLS])
                cls_name, message, payload = parse_tool.receive_from(self.gps.hard_port)
                print("Payload :", message)
                self.rec_msg = message
                if self.rec_msg != None:
                    return self.rec_msg
            except (ValueError, IOError) as err:
                #print('An error occured: ' ,err)
                #print('Trying again.....')
                continue

    def run(self):
        """
        Runs this class as a standalone service
        for testing purposes only
        """
        #self.getSatelliteConfiguration()
        temp = self.setSatelliteConfiguration(self.hexToBytes(self.disable_GPS))
        print("TempPayload :", temp)

if __name__ == '__main__':
    gnss_configurator = GnssConfigurator()
    gnss_configurator.run()
    
    