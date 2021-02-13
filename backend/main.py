from ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
import asyncio
import websockets
import websockets_routes
import datetime
import serial
import json


port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
gps = UbloxGps(port)

router = websockets_routes.Router()

# consider using different modules or classes for the configurations or replies to the messages
@router.route("/websocket")
async def listen_to_messages(websocket, path):
    try:
        while True:
            try:
                geo = gps.geo_coords()
                data = GnssData(datetime.datetime.now(), geo.lon, geo.lat, geo.flags.gnssFixOK, geo.height, geo.vAcc, geo.hAcc)
                gnssJSONData = json.dumps(data, indent=4, cls=GnssDataEncoder)
                await websocket.send(gnssJSONData)
                print(data)

            except (ValueError, IOError) as err:
                print(err)
    finally:
        port.close()

    #async for message in websocket:
        # right now it is only echoing the incoming messages, maybe some sort of switch-case?
        #print(message)
        #await websocket.send(message)


if __name__ == '__main__':
    start_server = websockets.serve(listen_to_messages, '192.168.178.44', 8765) # use the ip adress of the rasp, consider to check for the availability of the port number
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()