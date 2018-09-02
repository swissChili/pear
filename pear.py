import inquirer
import pear_server as pear
import requests
import threading
import ipaddress
import sys
import os
import socket

def prompt():
    return input("> ")
"""
ip = requests.get("http://bot.whatismyipaddress.com")
ip = ip.text
print("Your IP addr",ip)
"""

serve = [
    inquirer.List('method',
                   message="Create server or connect to one?",
                   choices=['Create', 'Connect'])
]
answers = inquirer.prompt(serve)
if answers['method'] == 'Connect':
    # connect to server
    print("connecting")
else:
    serve_info = [
        inquirer.Text('host',
                       message="Choose a host to serve on",
                       default="ipv6"),
        inquirer.Text('port',
                       message="Choose a port to serve on, defaults to 4792",
                       default="4792")
    ]
    host_info = inquirer.prompt(serve_info)
    if host_info['host'] == "ipv6":
        host_info['host'] = ''
    ip = host_info['host']
    port = int(host_info['port'])

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((ip, port))
        s.listen()
        conn, addr = s.accept()
        with conn:
            print('Connected by', addr)
            def prompter():
                data = prompt()
                conn.sendall(bytes(data + "\r\n", 'utf8'))
            def read():
                while True:
                    prompter()
                    data = conn.recv(1024)
                    if not data:
                        break
                    print(data.decode('utf8'))
            responder = threading.Thread(target = read())
            prompter = threading.Thread(target = prompter())
            prompter.start()
            responder.start()
            