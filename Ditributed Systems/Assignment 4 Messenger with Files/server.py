# Richard Watson CSC 376
# Assignment 4 Messenger with File Transfers


import sys
import socket
import receive
import struct
import os


def fileServer(port, fileName, mSocket):
    print("Server is on port: " + str(port))

    serverSocket = socket.socket(socket.AF_INET, socket.SOCKSTREAM)
    serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serverSocket.bind(("", port))
    serverSocket.listen(5)
    mSocket.send(fileName.encode())
    sock, addr = serverSocket.accept()
    serverSocket.close()
    fSizeBytes = sock.recv(4)
    if fSizeBytes:
        fSize = struct.unpack("!L", fSizeBytes[:4])[0]
        if fSize:
            receiveFile(sock, fileName[1:])
        else:
            print("File does not exist")
    else:
        print("File does not exist")
    sock.close()

def receiveFile(sock, fileName):
    file = open(fileName, "wb")
    while True:
        fileBytes = sock.recv(1024)
        if fileBytes:
            file.write(fileBytes)
        else:
            break
    file.close()


def run(sock, port):
    print("Enter an option ('m', 'f', 'x'):\n (M)essage (send)\n (F)ile (request)\n e(x)it")
    opt = sys.stdin.readline().strip().upper()
    if opt == "M":
        print("Enter your message:")
        mess = "m" + sys.stdin.readline().strip()
        sock.send(mess.encode())
    elif opt == "F":
        print("Which file do you want?")
        fileName = "f" + sys.stdin.readline().strip()
        fileServer(port, fileName, sock)
    elif opt == "X":
        os._exit(0)
    else:
        print("Incorrect option entered")
    run(sock, port)


def Server(port):
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serverSocket.bind(("", port))
    serverSocket.listen(5)
    sock, addr = serverSocket.accept()
    serverSocket.close()
    recThread = receive.Receive(sock).start()
    while (receive.portExists == False):
        i = 0
    sock.send(str(receive.filePortNumber).encode())
    run(sock, receive.filePortNumber)
