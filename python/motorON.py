import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
GPIO.setup(6, GPIO.OUT)
GPIO.setup(13, GPIO.OUT)
GPIO.setup(19, GPIO.OUT)
GPIO.setup(26, GPIO.OUT)


GPIO.output(6, True)
GPIO.output(26, True)
time.sleep(2)
GPIO.output(6, False)
GPIO.output(26, False) 
