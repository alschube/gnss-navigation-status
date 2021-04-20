import socket
import serial

TCP_IP = '192.168.178.44'
TCP_PORT = 8766
BUFFER_SIZE = 20

ser = serial.Serial('/dev/serial0', baudrate=38400, timeout=1)

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = ((TCP_IP, TCP_PORT))
sock.bind(server_address)
print('starting up on %s port %s' % server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print('waiting for a connection')
    connection, client_address = sock.accept()
    try:
        print('connection from', client_address)
        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(16)
            print('received "%s"' % data)
            if data:
                print('sending data over uart to receiver')
                ser.write(data)
            else:
                print('no more data from', client_address)
                break
            
    finally:
        # Clean up the connection
        connection.close()