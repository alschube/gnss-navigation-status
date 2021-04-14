import serial
from ublox_gps import sparkfun_predefines as sp
#import sparkfun_predefines as sp
from ublox_gps import core
from ublox_gps import UbloxGps

port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
gps = UbloxGps(port)

#GPS
enable_GPS='00 01 00 00 1f 00 31 10 01 fb 80'
disable_GPS='00 01 00 00 1f 00 31 10 00 fa 7f'

#GALILEO
enable_GAL='00 01 00 00 21 00 31 10 01 fd 8a'
disable_GAL='00 01 00 00 21 00 31 10 00 fc 89'

#GLONASS
enable_GLO='00 01 00 00 25 00 31 10 01 01 9e'
disable_GLO='00 01 00 00 25 00 31 10 00 00 9d'

#BEIDOU
enable_BDS='00 01 00 00 22 00 31 10 01 fe 8f'
disable_BDS='00 01 00 00 22 00 31 10 00 fd 8e'

def hexToBytes(byteString):
    hex_in_bytes = bytes.fromhex(byteString)
    print('Converted to: ', hex_in_bytes)
    return hex_in_bytes

def getSatelliteConfiguration():
    try:
        gps.send_message(sp.MON_CLS, gps.mon_ms.get('GNSS'))
        parse_tool = core.Parser([sp.MON_CLS, sp.ACK_CLS])
        cls_name, message, payload = parse_tool.receive_from(gps.hard_port)
        print(payload.enabled)
    except (ValueError, IOError) as err:
        print('An error occured: ' ,err)
        print('Trying again.....')
        getSatelliteConfiguration()
        
        
def setSatelliteConfiguration(bytesPayload):
    try:
        gps.send_message(sp.CFG_CLS, gps.cfg_ms.get('VALSET'), bytesPayload)
        parse_tool = core.Parser([sp.CFG_CLS, sp.ACK_CLS])
        cls_name, message, payload = parse_tool.receive_from(gps.hard_port)
        print(payload)
    except (ValueError, IOError) as err:
        print('An error occured: ' ,err)
        print('Trying again.....')
        setSatelliteConfiguration(bytesPayload)


if __name__ == '__main__':
    getSatelliteConfiguration()
    #setSatelliteConfiguration(hexToBytes(disable_BDS))
    
    