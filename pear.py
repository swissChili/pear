import inquirer
import pear_server as pear
import requests
import threading
import asyncio
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
            async def input_handler():
                conn.sendall(sys.stdin.readline())
            async def read():
                while True:
                    data = conn.recv(1024)
                    if not data:
                        break
                    print(data.decode('utf8'))
            tasks = [
                asyncio.ensure_future(read()),
            ]
            loop = asyncio.get_event_loop()
            loop.add_reader(sys.stdin, input_handler)
            loop.run_until_complete(asyncio.wait(tasks))  
            loop.close()