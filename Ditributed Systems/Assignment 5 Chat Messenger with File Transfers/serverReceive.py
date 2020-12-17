# Richard Watson
# CSC 376 Assignment 5: Chat With File Transfers



import os
import sys
import struct
import socket
import threading


global clientList
clientList = []


class Receive(threading.Thread):
    def __init__(self, sock):
        threading.Thread.__init__(self)
        self.sock = sock
        self.client = {"name": "", "port": 0, "socket": None}


    def sendZeroSize(self):
        zeroBytes = struct.pack("!L", 0)
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.connect(("", self.client["port"]))
        print(self.client["name"] + " is connected")
        sock.send(zeroBytes)
        sock.shutdown(socket.SHUT_WR)
        sock.close()
        return

    def receiveFile(self, incomingSock, fName, fSize):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.connect(("", self.client["port"]))
        print("serverReceive: Connected on port: " + str(self.client["port"]))
        fSizeBytes = struct.pack("!L", fSize)
        sock.send(fSizeBytes)
        print("sending file " + fName + " with size: " + str(fSize))
        fBytes = incomingSock.recv(1024)
        while fBytes:
            if fBytes:
                sock.send(fBytes)
            fBytes = incomingSock.recv(1024)
        sock.close()
        return



    def file_server(self, portNumber, fName, mSock):
        serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        serversocket.bind(("", portNumber))
        print("binded on: " + str(portNumber))
        serversocket.listen(5)
        mSock.send(("f" + fName).encode())
        sock, addr = serversocket.accept()
        serversocket.close()
        fSizeBytes = sock.recv(4)
        if fSizeBytes:
            fSize = struct.unpack("!L", fSizeBytes[:4])[0]
            if fSize:
                self.receiveFile(sock, fName, fSize)
            else:
                print("File not found or is of size 0")
                self.sendZeroSize()
        else:
            print("File not found or is of size 0")
            self.sendZeroSize()
        sock.close()
        return


    def run(self):
        name = self.sock.recv(1024).decode()
        lPort = int(self.sock.recv(1024).decode())
        self.client = {"name": name, "port": lPort, "socket": self.sock}
        clientList.append(self.client)
        while True:
            try:
                msgBytes = self.sock.recv(1024)
                message = msgBytes.decode()
                if (len(message) > 0):
                    if message[0] == "m":
                        message = name + ": " + message[1:]
                        print(message)
                        for client in clientList:
                            if client["socket"] != self.sock:
                                client["socket"].send(("m" + message).encode())
                    elif message[0] == "f":
                        ownerName = message[1:]
                        fileName = self.sock.recv(1024).decode()
                        print("Owner Name: " + ownerName)
                        print("File Name: " + fileName)
                        for client in clientList:
                            if client["name"] == ownerName:
                                ownerPort = client["port"]
                                ownerSocket = client["socket"]
                                print("Requesting file: " + fileName + " from: " + ownerName + " on port: " + str(ownerPort))
                                self.file_server(ownerPort, fileName, ownerSocket)

                    elif message == "exit":
                        self.client["socket"].close
                        clientList.remove(self.client)
            except Exception as e:
                print(e)
