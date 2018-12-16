import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(24, GPIO.OUT)

def FanOn() :
    GPIO.output(24, True)

def FanOff() :
    GPIO.output(24, False)
    
