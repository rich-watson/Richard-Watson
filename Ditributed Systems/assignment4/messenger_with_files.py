# Richard Watson CSC 376
# Assignment 4: Messneger with File Transfers


import sys
import client, server


args = sys.argv
argsNum = len(args)
serverAdd = "localhost"
serverFlag = True

i = 0

while i < argsNum:
    if args[i] == "-l":
        listeningPort = int(args[i+1])

    if args[i] == "-s":
        serverAdd = int(args[i+1])
        serverFlag = False

    if args[i] == "-p":
        serverPort = int(args[i+1])
        serverFlag = False

    i += 1

if (serverFlag):
    server.Server(listeningPort)
else:
    client.Client(listeningPort, serverPort, serverAdd)
