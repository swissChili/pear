import pear_server as ps
import requests
import ipaddress
from bottle import request, post, get, run

def prompt():
    return input("> ")

ip = requests.get("http://bot.whatismyipaddress.com")
ip = ip.text
print("Your IP addr",ip)

@post("/pear")
def on_msg():
    postdata = request.body.read()
    print(postdata) 

run(host=ip, port=2307)
