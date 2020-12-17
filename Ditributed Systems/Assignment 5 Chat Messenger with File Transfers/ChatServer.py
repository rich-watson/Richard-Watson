# Richard Watson
# CSC 376 Assignment 5: Chat With File Transfers


import sys
import socket
import serverReceive



args = sys.argv
portNumber = int(args[1])

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversocket.bind(("", portNumber))
print("Chat Server binded on: " + str(portNumber))
serversocket.listen(5)


while True:
    sock, addr = serversocket.accept()
    serverReceive.Receive(sock).start()
