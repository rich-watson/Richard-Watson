# Richard Watson
# CSC 376 Assignment 5: Chat With File Transfers

import threading
import os
import struct
import socket

class Receive(threading.Thread):
    def __init__(self, sock, lPort):
        threading.Thread.__init__(self)
        self.sock = sock
        self.lPort = lPort


    def sendFile(self, sock, fSize, fileName):
        fSizeBytes = struct.pack("!L", fSize)
        sock.send(fSizeBytes)
        while True:
            fBytes = fileName.read(1024)
            if fBytes:
                sock.send(fBytes)
            else:
                break
            fileName.close()
#            sock.close()
            return


    def fileClient(self, sock, portNumber, fSize, fileName):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.connect(("", portNumber))
        self.sendFile(sock, fSize, fileName)
        sock.close()
        return


    def noFile(self, sock, portNumber):
        print(" clientReceive: File does not exist")
        empty = struct.pack("!L", 0)
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.connect(("", portNumber))
        sock.send(empty)
        sock.shutdown(socket.SHUT_WR)
        sock.close()
        return


    def checkForFile(self, sock, fPortNumber, fName):
        try:
            file_stat = os.stat(fName)
            if (file_stat.st_size):
                print(fName + " exists")
                file = open(fName, "rb")
                self.fileClient(sock, fPortNumber, file_stat.st_size, file)
            else:
                self.noFile(sock, fPortNumber)
        except:
            self.noFile(sock, fPortNumber)


    def run(self):
        while True:
            msgBytes = self.sock.recv(1024)
            message = msgBytes.decode()
            if (len(message) > 0):
                if message[0] == "m":
                    print(message[1:])
                elif message[0] == "f":
                    fName = message[1:]
                    self.checkForFile(self.sock, self.lPort, fName)
        os._exit(0)
