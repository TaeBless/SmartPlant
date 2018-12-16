import RPi.GPIO as GPIO
import time
import mcp3008

moistureCh =20
GPIO.setmode(GPIO.BCM)
GPIO.setup(moistureCh, GPIO.IN)
GPIO.setup(12, GPIO.OUT)

def WaterOn() :
    value = mcp3008.readadc(moistureCh)
    
    if value > 500:
        GPIO.output(12, False)
    else :
        GPIO.output(12, True)
    
