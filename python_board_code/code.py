import board
import adafruit_mlx90614
import json

from ideaboard import IdeaBoard
import time

import socketpool
import ssl
import wifi
import adafruit_requests as requests

socket = socketpool.SocketPool(wifi.radio)
https = requests.Session(socket, ssl.create_default_context())

print("Connecting...")
wifi.radio.connect("BaradDur", "L4#r3d#d3#4b4j0")
print("Connected to Wifi!")

ib = IdeaBoard()

i2c = board.I2C()
mlx = adafruit_mlx90614.MLX90614(i2c)
boton = ib.DigitalIn(board.IO27)

AZUL = (0,0,255)
NEGRO = (0,0,0)

while True:
    if(boton.value == False):
        ib.pixel = AZUL
        login_request = {
            "email": "pedro",
            "password":"test123"
        }
        tokenResponse = https.post('https://venus-api.azurewebsites.net/rest/auth/login',json=login_request)
        
        json_token = json.loads(tokenResponse.text)
        
        tempRequest = [{
            "fieldName":"bodyTemperature",
            "value": mlx.object_temperature
        }]
        
        headers = {
            "Authorization":"Bearer {token}".format(token = json_token["token"]),
        }
        
        tempResponse = https.post('https://venus-api.azurewebsites.net/rest/period-criteria/create',
                                  json=tempRequest,headers=headers)
        if tempResponse.status_code == 200:
            print('Request was successful!')
            print(tempResponse.text)  
        else:
            print('Request failed with status code:', tempResponse.status_code)
        ib.pixel = NEGRO

