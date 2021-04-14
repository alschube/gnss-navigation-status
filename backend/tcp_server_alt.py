import socketserver
import serial



class Handler_TCPServer(socketserver.BaseRequestHandler):
    """
    The TCP Server class for demonstration.

    Note: We need to implement the Handle method to exchange data
    with TCP client.

    """
    
    s = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)

    def handle(self):
        # self.request - TCP socket connected to the client
        self.data = self.request.recv(2048)#.strip()
        #print("{} sent:".format(self.client_address[0]))
        #print(self.data)
        self.s.write(self.data)
        #print(self.s.read(205))
        # just send back ACK for data arrival confirmation
        self.request.sendall("ACK from TCP Server".encode())

if __name__ == "__main__":
    HOST, PORT = "192.168.178.44", 8766

    # Init the TCP server object, bind it to the localhost on 9999 port
    tcp_server = socketserver.TCPServer((HOST, PORT), Handler_TCPServer)

    # Activate the TCP server.
    # To abort the TCP server, press Ctrl-C.
    tcp_server.serve_forever()