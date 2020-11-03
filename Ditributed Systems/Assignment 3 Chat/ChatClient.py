# Richard Watson
# CSC 376 Assignment 3: Chat
# 10/12/20


import sys
import os
import socket
import threading



def clientReceive(sock):
    msg_bytes = sock.recv(1024)
    while True:
        print(msg_bytes.decode())
        msg_bytes = sock.recv(1024)
    os._exit(0)

def clientSend(sock, message):
    while message:
        sock.send(message.encode())
        message = sys.stdin.readline().strip("\n")
    os._exit(0)

def client(port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print("Waiting on the server...")
    sock.connect(("localhost", port))
    print("What is your name?")
    name = sys.stdin.readline().strip("\n")
    print("Sending your name to server...")
    rec_thread = threading.Thread(target=clientReceive, args=(sock,)).start()
    send_thread = threading.Thread(target=clientSend, args=(sock, name)).start()





if __name__ == "__main__":
    args = sys.argv
    port = int(args[1])
    client(port)

    
