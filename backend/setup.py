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

"""
This script is responsible for enabling all required messages and ports when executed
in order to run the app correctly.

This has to be done only once when the receiver is not configured.
"""

import serial
from ublox_gps import sparkfun_predefines as sp
from ublox_gps import core
from ublox_gps import UbloxGps

# Create a serial for communication
port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
gps = UbloxGps(port)

CFG_UART1OUTPROT_UBX_RAM = '00 01 00 00 01 00 74 10 01 20 b3'
CFG_UART1OUTPROT_UBX_FLASH = '00 04 00 00 01 00 74 10 01 23 cb'

CFG_UART1INPROT_RTCM3X_RAM = '00 01 00 00 04 00 73 10 01 22 bf'
CFG_UART1INPROT_RTCM3X_FLASH = '00 04 00 00 04 00 73 10 01 25 d7'

CFG_MSGOUT_UBX_RXM_RTCM_UART1_RAM = '00 01 00 00 69 02 91 20 01 b7 3a'
CFG_MSGOUT_UBX_RXM_RTCM_UART1_FLASH = '00 04 00 00 69 02 91 20 01 ba 52'

CFG_MSGOUT_UBX_NAV_SAT_UART1_RAM = '00 01 00 00 16 00 91 20 01 62 93'
CFG_MSGOUT_UBX_NAV_SAT_UART1_FLASH = '00 04 00 00 16 00 91 20 01 65 ab'

CFG_MSGOUT_UBX_NAV_PVT_UART1_RAM = '00 01 00 00 07 00 91 20 01 53 48'
CFG_MSGOUT_UBX_NAV_PVT_UART1_FLASH = '00 04 00 00 07 00 91 20 01 56 60'

def createMsgArray():
    """
    This method creates a list for all messages to send
    
    :return: the list
    :rtype: list
    """
    msgList = [CFG_UART1OUTPROT_UBX_RAM, CFG_UART1OUTPROT_UBX_FLASH,
               CFG_UART1INPROT_RTCM3X_RAM, CFG_UART1INPROT_RTCM3X_FLASH,
               CFG_MSGOUT_UBX_RXM_RTCM_UART1_RAM,CFG_MSGOUT_UBX_RXM_RTCM_UART1_FLASH,
               CFG_MSGOUT_UBX_NAV_SAT_UART1_RAM,
               CFG_MSGOUT_UBX_NAV_SAT_UART1_FLASH,
               CFG_MSGOUT_UBX_NAV_PVT_UART1_RAM,
               CFG_MSGOUT_UBX_NAV_PVT_UART1_FLASH]
    return msgList

def hexToBytes(byteString):
    """
    This method converts a hex string into bytes
        
    :param byteString:   the string to convert into bytes
        
    :return: the converted string
    :rtype: bytes
    """

    hex_in_bytes = bytes.fromhex(byteString)
    return hex_in_bytes

def sendMessage(bytesPayload):
    """
    This method sends the required configuration messages to the receiver
    
    :param bytesPayload: the bytes to send
    """
    while True:
        try:
            gps.send_message(sp.CFG_CLS, gps.cfg_ms.get('VALSET'), bytesPayload)
            parse_tool = core.Parser([sp.CFG_CLS, sp.ACK_CLS])
            cls_name, message, payload = parse_tool.receive_from(gps.hard_port)
            print("Payload: ", message)
            if message == 'ACK':
                print('Success\n')
                break
        except (ValueError, IOError) as err:
            print('An error occured, trying again...')
            continue

def run():
    """
    This method runs this script
    """
    messages = createMsgArray()
    for m in messages:
        print('Sending: ', m)
        sendMessage(hexToBytes(m))

if __name__ == '__main__':
    run()
