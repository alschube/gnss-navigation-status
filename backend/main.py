import asyncio
# need to install via pip on rasp (evtl you'll have to upgrade pip on the rasp)
import websockets
import websockets_routes

router = websockets_routes.Router()

# consider using different modules or classes for the configurations or replies to the messages
@router.route("/websocket")
async def listen_to_messages(websocket, path):
    async for message in websocket:
        # right now it is only echoing the incoming messages, maybe some sort of switch-case?
        print(message)
        await websocket.send(message)

if __name__ == '__main__':
    start_server = websockets.serve(listen_to_messages, '192.168.178.48', 8765) # use the ip adress of the rasp, consider to check for the availability of the port number
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()