import machine
import time
import urequests
import ujson
import network
import dht

# Define your Wi-Fi credentials
wifi_ssid = 'Wokwi-GUEST'
wifi_password = ''  # Replace with the actual Wi-Fi password

# Connect to Wi-Fi
wifi = network.WLAN(network.STA_IF)
wifi.active(True)
wifi.connect(wifi_ssid, wifi_password)

# Wait for Wi-Fi connection
while not wifi.isconnected():
    pass

# Define GPIO pins
TRIG_PIN = machine.Pin(15)
ECHO_PIN = machine.Pin(2)
alarm_pin = machine.Pin(5, machine.Pin.OUT)
buzzpin = machine.Pin(14, machine.Pin.OUT)
DHT_PIN = machine.Pin(18)

# Set trig pin as an output
TRIG_PIN.init(machine.Pin.OUT)

# Set echo pin as an input
ECHO_PIN.init(machine.Pin.IN)

# Set the alarm pin initially to OFF
alarm_pin.off()

# Maximum water level for alarm (in cm)
alarm_threshold = 100  # Adjust this value as needed

# Maximum parking distance
max_distance = 50

def read_dht_sensor():
    d = dht.DHT22(DHT_PIN)
    d.measure()
    return d.temperature(), d.humidity()

def measure_distance():
    # Trigger ultrasonic sensor
    TRIG_PIN.on()
    time.sleep_us(10)
    TRIG_PIN.off()

    # Wait for echo to be HIGH (start time)
    while not ECHO_PIN.value():
        pass
    pulse_start = time.ticks_us()

    # Wait for echo to be LOW (end time)
    while ECHO_PIN.value():
        pass
    pulse_end = time.ticks_us()

    # Calculate distance
    pulse_duration = time.ticks_diff(pulse_end, pulse_start)
    distance = pulse_duration / 58  # Speed of sound (343 m/s) divided by 2

    return distance

buzz_start_time = None 

# Firebase Realtime Database URL and secret
firebase_url = 'https://flood-monitoring-511bd-default-rtdb.asia-southeast1.firebasedatabase.app/'  # Replace with your Firebase URL
firebase_secret = 'jnMJGSFxTcAMawQ6b0yUZp4SitouPTTy5p0Ql4aG'  # Replace with your Firebase secret

# Function to send data to Firebase
def send_data_to_firebase(distance, temperature, humidity, status):
    data = {
        "Distance": distance,
        "Temperature": temperature,
        "Humidity": humidity,
        "Status": status
    }
    url = f'{firebase_url}/sensor_data.json?auth={firebase_secret}'
    
    try:
        response = urequests.patch(url, json=data)  # Use 'patch' instead of 'put'
        if response.status_code == 200:
            print("Data sent to Firebase")
        else:
            print(f"Failed to send data to Firebase. Status code: {response.status_code}")
    except Exception as e:
        print(f"Error sending data to Firebase: {str(e)}")

while True:
    dist = measure_distance()
    temp, humidity = read_dht_sensor()

    # Check if the distance is less than a threshold (e.g., 50 cm)
    if dist < max_distance:
        # Turn on the buzzer and alarm
        buzzpin.on()
        alarm_pin.on()
        status = "Flood Alert"
        buzz_start_time = time.ticks_ms()
    elif buzz_start_time is not None and time.ticks_diff(time.ticks_ms(), buzz_start_time) >= 60000:  # 1 minute
        # Turn off the buzzer and alarm after 1 minute
        buzzpin.off()
        alarm_pin.off()
        status = "No Flood Alert"
    else:
        status = "No Flood Alert"

    print(f"Distance: {dist:.2f} cm")
    print(f"Temperature: {temp:.2f}°C, Humidity: {humidity:.2f}%")
    print("Status:", status)

    # Send data to Firebase
    send_data_to_firebase(dist, temp, humidity, status)

    time.sleep(2)  # Adjust the sleep duration as needed
