import FarmLed
import FarmTemp
import FarmWater

import Adafruit_DHT
import RPi.GPIO as GPIO
import time
import sys

GPIO.setmode(GPIO.BCM)

temp = int(sys.argv[1])
hum = int(sys.argv[2])

FarmTemp.Temp(temp, hum)
FarmWater.WaterOn()

