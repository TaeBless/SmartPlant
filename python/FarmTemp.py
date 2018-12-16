import Adafruit_DHT
import FarmMotor
import FarmFan

sensor = Adafruit_DHT.DHT11
pin = 21

def Temp(tem, hum):
    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
    if humidity is not None and temperature is not None :
            
        if temperature > tem :
            FarmMotor.up()

        if temperature < tem :
            FarmMotor.down()

        if humidity > hum :
            FarmFan.FanOn()

        if humidity < hum :
            FarmFan.FanOff()

        print temperature, humidity

    else:
        print 'Failed to get reading. Try Again!'
