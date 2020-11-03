# Richard Watson
# CSC 376 Assignment 3: Chat
# 10/12/20

import sys
import socket
import os
import threading


def servReceive(sock):

    name = sock.recv(1024).decode()
    msg_bytes = sock.recv(1024)
    while msg_bytes:
        mess = name + ": " + msg_bytes.decode()
        for client in clients:
            if client != sock:
                client.send(mess.encode())
        msg_bytes = sock.recv(1024)

    clients.remove(sock)



def server(port):
    servSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    servSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    servSocket.bind(('', port))
    servSocket.listen(5)
    print("server listening...")


    while True:
        sock, addr = servSocket.accept()
        rec_thread = threading.Thread(target=servReceive, args=(sock,)).start()
        clients.append(sock)




if __name__ == "__main__":
    clients = []
    args = sys.argv
    port = int(args[1])
    server(port)
