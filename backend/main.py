from ublox_gps import UbloxGps
from gnss_data import GnssData
from gnss_data_encoder import GnssDataEncoder
from satellite_data import SatelliteData
import asyncio           
import websockets
import websockets_routes
import datetime
import serial
import json


port = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)
gps = UbloxGps(port)

router = websockets_routes.Router()

data = GnssData()

# consider using different modules or classes for the configurations or replies to the messages
@router.route("/websocket")
async def listen_to_messages(websocket, path):
    global data
    try:
        while True:
            try:    
                try:
                    raw_data = gps.geo_coords()
                    data.time = str(datetime.datetime.now())
                    data.longitude = raw_data.lon
                    data.latitude = raw_data.lat
                    data.gnss_fix_ok = raw_data.flags.gnssFixOK
                    data.height = raw_data.hMSL
                    data.v_acc = raw_data.vAcc
                    data.h_acc = raw_data.hAcc
                    data.num_sats_fixed = raw_data.numSV

                    raw_data = gps.satellites()
                    data.num_sats_total = raw_data.numSvs
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
                    
                    data.satellites = satellites
                    gnssJSONData = json.dumps(data.to_dict(), indent=4, cls=GnssDataEncoder)
                    await websocket.send(gnssJSONData)
                    print(gnssJSONData)
                    data = GnssData()
                except(AttributeError):
                    print("no data found")
                    continue
            
                
            


            except (ValueError, IOError) as err:
                print(err)
    finally:
        port.close()
        
if __name__ == '__main__':
    start_server = websockets.serve(listen_to_messages, '192.168.178.44', 8765) # use the ip adress of the rasp, consider to check for the availability of the port number
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()