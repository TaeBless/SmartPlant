import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
GPIO.setup(6, GPIO.OUT)
GPIO.setup(13, GPIO.OUT)
GPIO.setup(19, GPIO.OUT)
GPIO.setup(26, GPIO.OUT)
GPIO.setup(17, GPIO.OUT)
GPIO.setup(27, GPIO.OUT)
GPIO.setup(22, GPIO.OUT)
GPIO.setup(12, GPIO.OUT)
GPIO.setup(24, GPIO.OUT)

GPIO.output(13, True)
GPIO.output(19, True)    
time.sleep(0.3)
GPIO.output(13, False)
GPIO.output(19, False)

GPIO.output(17,False);
GPIO.output(27,False);
GPIO.output(22,False);
GPIO.output(12, False)
GPIO.output(24, False)
