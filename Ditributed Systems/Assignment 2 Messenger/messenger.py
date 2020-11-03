# Richard Watson CSC 376 Assignment 2: Messenger
# September 30, 2020

import sys
import socket
import os
import threading


def receive (sock):
    msg_bytes = sock.recv(1024)
    while True:
        print(msg_bytes.decode())
        msg_bytes = sock.recv(1024)
    os._exit(0)


def send (sock, message):
    while message:
        sock.send(message.encode())
        message = sys.stdin.readline().strip("\n")
    os._exit(0)

def server (port):
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serversocket.bind(('', port))
    serversocket.listen(5)
    sock, addr = serversocket.accept()
    serversocket.close()
    rec_thread = threading.Thread(target=receive, args=(sock,)).start()
    message = sys.stdin.readline().strip("\n")
    send_thread = threading.Thread(target=send, args=(sock, message)).start()


def client (port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('', port))
    rec_thread = threading.Thread(target=receive, args=(sock,)).start()
    message = sys.stdin.readline().strip("\n")
    send_thread = threading.Thread(target=send, args=(sock, message)).start()



if __name__ == "__main__":
    argc = len(sys.argv)
    if argc > 3:
        sys.exit()
    if "-l" in sys.argv[1]:
        port = int(sys.argv[2])
        server(port)
    else:
        port = int(sys.argv[1])
        client(port)
    sys.exit()
