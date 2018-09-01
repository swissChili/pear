import bottle
import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
import ast

def gen_rsa():
    random_generator = Random.new().read
    key = RSA.generate(1024, random_generator)
    pub = key.publickey()
    return {
            "key": key,
            "public": pub
        }
    
def encrypt(pub, msg):
    enc = pub.encrypt(msg, 32)
    return str(enc)

def decrypt(key, msg):
    key.decrypt(ast.literal.eval(str(msg)))

