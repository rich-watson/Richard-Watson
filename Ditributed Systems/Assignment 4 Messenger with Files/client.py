# Richard Watson CSC 376
# Assignment 4 Messenger with File Transfers


import sys
import socket
import receive
import struct
import os



def receiveFile(sock, fName):
    file = open(fName, "wb")

    while True:
        fileBytes = sock.recv(1024)
        if fileBytes:
            file.write(fileBytes)
        else:
            break
    file.close()


def fileServer(port, fName, mSock):
    print("Server on port: " + str(port))
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serverSocket.bind(("", port))
    serverSocket.listen(5)
    mSock.send(fName.encode())
    sock, addr = serverSocket.accept()
    serverSocket.shutdown(socket.SHUT_WR)
    serverSocket.close()
    fSizeBytes = sock.recv(4)
    if fSizeBytes:
        fSize = struct.unpack("!L", fSizeBytes[:4])[0]
        if fSize:
            receiveFile(sock, fName[1:])
        else:
            print("File is empty or isn't there")
    else:
        print("File is empty or isn't there")
    sock.close()


def run(sock, port):
    print("Enter an option ('m', 'f', 'x'):\n (M)essage (send)\n (F)ile (request)\ne(X)it")
    opt = sys.stdin.readline().strip().upper()
    if opt == "M":
        print("Enter your message:")
        mess = "m" + sys.stdin.readline().strip()
        sock.send(mess.encode())
    elif opt == "F":
        print("Which file do you want?")
        fName = "f" + sys.stdin.readline().strip()
        fileServer(port, fName, sock)
    elif opt == "X":
        os._exit(0)
    else:
        print(opt + "Incorrect option entered")
    run(sock, port)


def Client(listeningPort, serverPort, serverAdd):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((serverAdd, serverPort))
    recThread = receive.Receive(sock).start()
    sock.send(str(listeningPort).encode())
    while (receive.portExists == False):
        i = 0
    receive.filePortNumber = listeningPort
    run(sock, listeningPort)
