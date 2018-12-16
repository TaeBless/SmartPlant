import RPi.GPIO as GPIO
from pyfcm import FCMNotification
import sys
import time

GPIO.setmode(GPIO.BCM)
moveCh = 5
GPIO.setup(moveCh, GPIO.IN)
GPIO.setup(18, GPIO.OUT)

push_service = FCMNotification(api_key="AAAAXNrNE7k:APA91bGwuKJ7R0G-2ynjf64WZzMuSLRRzRxRV40wXZkPFE0l57iGqE7a7UsgE_WcoR6DFTyf4zb_RZ18PP0RvndP4LBVF6FD04QZ4WRf08mnCsX9RmgxlGvDB7FuvSqgMn7DBnOYThcn")
data_message = {}

idToken = sys.argv[1]
print(idToken)

def handler(channel) :
    push_service.notify_single_device(registration_id=idToken, message_title="!!!!warning!!!!", message_body="detect")
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)
    GPIO.output(18, True)
    time.sleep(0.5)
    GPIO.output(18, False)
    time.sleep(0.1)

GPIO.add_event_detect(moveCh, GPIO.RISING, callback=handler)

while True:
    time.sleep(5)
