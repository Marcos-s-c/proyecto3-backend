import board
import adafruit_mlx90614
import json

from ideaboard import IdeaBoard
import time

import socketpool
import ssl
import wifi
import adafruit_requests as requests
import random as rnd

socket = socketpool.SocketPool(wifi.radio)
https = requests.Session(socket, ssl.create_default_context())

print("Connecting...")
wifi.radio.connect("CENFO EXPO", "Expocenfo2023")
print("Connected to Wifi!")

ib = IdeaBoard()

i2c = board.I2C()
mlx = adafruit_mlx90614.MLX90614(i2c)
boton = ib.DigitalIn(board.IO27)

AZUL = (0,0,255)
NEGRO = (0,0,0)
RED = (255,0,0)

while True:
    if(boton.value == False):
        ib.pixel = AZUL
        login_request = {
            "email": "mmendezr@ucenfotec.ac.cr",
            "password":"test"
        }
        tokenResponse = https.post('https://venus-api.azurewebsites.net/rest/auth/login',json=login_request)
        json_token = json.loads(tokenResponse.text)


        headers = {
            "Authorization":"Bearer {token}".format(token = json_token["token"]),
        }

        capturedTemperature = mlx.object_temperature
        if capturedTemperature < 35 and capturedTemperature > 42:
            ib.pixel = RED
            print("Invalid Temperature, please try again")
            continue

        print("TEMPERATURA {:.2f}".format(capturedTemperature))
        tempRequest = [{
            "fieldName":"temperature",
            "value": "{:.2f}".format(capturedTemperature)
        }]

        tempResponse = https.post('https://venus-api.azurewebsites.net/rest/period-criteria/create',
                                  json=tempRequest,headers=headers)
        if tempResponse.status_code == 200:
            print('Request was successful!')
            print(tempResponse.text)
        else:
            print('Request failed with status code:', tempResponse.status_code)
        ib.pixel = NEGRO


