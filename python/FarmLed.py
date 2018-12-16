import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(17,GPIO.OUT)
GPIO.setup(27,GPIO.OUT)
GPIO.setup(22,GPIO.OUT)

def led(mode):
    if mode=="move":
        GPIO.output(17,True);
        GPIO.output(27,True);
        GPIO.output(22,True);
    else:
        GPIO.output(17,False);
        GPIO.output(27,False);
        GPIO.output(22,False);

