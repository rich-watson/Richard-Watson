# Richard Watson
# CSC 376 Assignment 5: Chat With File Transfers

import sys
import socket
import time
import os
import struct
import clientReceive



def file_server (port, fName, mainSocket):
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serversocket.bind(('', port))
    print("Chat Client binded on: " + str(port))
    serversocket.listen(5)
    mainSocket.send(fName.encode())
    sock, addr = serversocket.accept()
    serversocket.close()
    fSizeBytes = sock.recv(4)
    if fSizeBytes:
        fSize = struct.unpack("!L", fSizeBytes[:4])[0]
        if fSize:
            receive_file(sock, fName)
        else:
            print("File not found or is of size 0")
    else:
        print("File not found or is of size 0")
    sock.close()

def receive_file (sock, fName):
    file = open(fName, "wb")
    while True:
        fBytes = sock.recv(1024)
        if fBytes:
            file.write(fBytes)
        else:
            break
    file.close()



def run(sock, port):
    optsMessage = "Enter an option ('m', 'f', 'x'):\n (M)essage (send)\n (F)ile (request)\ne(X)it"
    print(optsMessage)
    option = sys.stdin.readline().strip().upper()
    if option == "M":
        print("Enter your message:")
        message = "m" + sys.stdin.readline().strip()
        sock.send(message.encode())
    elif option == "F":
        print("Who owns the file?")
        fileOwner = "f" + sys.stdin.readline().strip()
        sock.send(fileOwner.encode())
        print("Which file do you want?")
        fName = sys.stdin.readline().strip()
        file_server(port, fName, sock)
    elif option == "X":
        message = "exit"
        sock.send(message.encode())
        os._exit(0)
    else:
        print("Option entered is not valid")
    run(sock, port)


args = sys.argv
i = 1
while (i < len(args)):
    if (args[i] == "-p"):
        sPort = int(args[i + 1])
    if (args[i] == "-l"):
        lPort = int(args[i + 1])
    i += 1


sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("Waiting on server")
sock.connect(("", sPort))
print("What is your name?")
name = sys.stdin.readline().strip()
print("Sending your name to the server")


sock.send(name.encode())
time.sleep(.5)
sock.send(str(lPort).encode())


recThread = clientReceive.Receive(sock, lPort).start()

run(sock, lPort)
