# Richard Watson CSC 376
# Assignment 4 Messenger with File Transfers


import threading
import os
import struct
import socket

global portExists
portExists = False


class Receive(threading.Thread):
    def __init__(self, sock):
        threading.Thread.__init__(self)
        self.sock = socket



    def sendFile(self, sock, fSize, file):
        print("File size: " + str(fSize))
        fSizeBytes = struct.pack("!L", fSize)
        sock.send(fSizeBytes)
        while True:
            fileBytes = file.read(1024)
            if fileBytes:
                sock.send(fileBytes)
            else:
                break
        file.close()


    def fileClientSide(self, sock, port, fSize, file):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(("localhost", port))
        self.sendFile(sock, fSize, file)
        sock.shutdown(socket.SHUT_WR)
        sock.close()



    def emptyFile(self, sock, port):
        print("There is no file")
        emptyBytes = struct.pack("!L", 0)
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(("localhost", port))
        sock.send(emptyBytes)
        sock.shutdown(socket.SHUT_WR)
        sock.close()


    def fileLook(self, sock, fPort, fName):
        print(fName)
        try:
            fileStat = os.stat(fName)
            if fileStat.st_size:
                file = open(fName, "rb")
                self.fileClientSide(sock, fPort, fileStat.st_size, file)
            else:
                self.emptyFile(sock, fPort)
        except:
            self.emptyFile(sock, fPort)


    def run(self):
        global filePortNumber
        global portExists
        filePortNumber = int(self.sock.recv(1024).decode())
        portExists = True

        while True:
            msgBytes = self.sock.recv(1024)
            mess = msgBytes.decode()
            if len(mess) > 0:
                if mess[0] == "m":
                    print(mess[1:])
                elif mess[0] == "f":
                    print("Request for file: " + mess[1:] + "Port: " + str(filePortNumber))
                    self.fileLook(self.sock, filePortNumber, mess[1:])
            else:
                os._exit(0)
