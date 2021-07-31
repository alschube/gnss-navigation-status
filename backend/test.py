import serial
import time
from ublox_gps import sparkfun_predefines as sp
#import sparkfun_predefines as sp
from gnss_data_fetcher import DataFetcher
from ublox_gps import core
from ublox_gps import UbloxGps

port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
gps = UbloxGps(port)

nav_pvt_uart1_flash=   '00 04 00 00 07 00 91 20 01 56 60'
nav_pvt_uart1_ram  =   '00 01 00 00 07 00 91 20 01 53 48'
nav_pvt_uart1_bbr=     '00 02 00 00 07 00 91 20 01 54 50'
nav_sat_uart1_flash =  '00 04 00 00 16 00 91 20 01 65 ab'
nav_sat_uart1_ram =    '00 01 00 00 16 00 91 20 01 62 93'
nav_sat_uart1_bbr =    '00 02 00 00 16 00 91 20 01 63 9b'
rxm_rawx_uart1_flash = '00 04 00 00 a5 02 91 20 01 f6 7e'
rxm_rawx_uart1_ram =   '00 01 00 00 a5 02 91 20 01 f3 66'
rxm_rawx_uart1_bbr =   '00 02 00 00 a5 02 91 20 01 f4 6e'
uart1in_rtcm3_flash =  '00 04 00 00 04 00 73 10 01 25 d7'
uart1in_rtcm3_ram =    '00 01 00 00 04 00 73 10 01 22 bf'
uart1in_rtcm3_bbr =    '00 02 00 00 04 00 73 10 01 23 c7'

messages = [nav_pvt_uart1_ram, nav_pvt_uart1_bbr, nav_pvt_uart1_flash, nav_sat_uart1_flash, nav_sat_uart1_ram, nav_sat_uart1_bbr, rxm_rawx_uart1_flash, rxm_rawx_uart1_ram, rxm_rawx_uart1_bbr, uart1in_rtcm3_flash, uart1in_rtcm3_ram, uart1in_rtcm3_bbr]

def hexToBytes(byteString):
        hex_in_bytes = bytes.fromhex(byteString)
        print('Converted to: ', hex_in_bytes)
        return hex_in_bytes

if __name__ == '__main__':
    for msg in messages:
        try:
            gps.send_message(sp.CFG_CLS, gps.cfg_ms.get('VALSET'), hexToBytes(msg))
            parse_tool = core.Parser([sp.CFG_CLS, sp.ACK_CLS])
            cls_name, message, payload = parse_tool.receive_from(gps.hard_port)
            print("Payload :", message)
            rec_msg = message
            time.sleep(1)
        except (ValueError) as err:
            print(err)
            continue
        except(IOError) as err:
            print(err)
            continue