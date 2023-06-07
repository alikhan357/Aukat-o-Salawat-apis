import os
import requests
import pygame
import time
from datetime import datetime, timedelta


REMINDERS_API_URL = "http://localhost:5000/api/v1/reminder/pi/reminders/{}"


def play(audio):
    pygame.init()
    my_sound = pygame.mixer.Sound(audio)
    my_sound.play()
    time.sleep(300000)

def download_mp3(url, save_path):
    if not os.path.exists(save_path):
        response = requests.get(url)
        if response.status_code == 200:
            with open(save_path, 'wb') as file:
                file.write(response.content)
            print('MP3 file downloaded and saved successfully.')
        else:
            print('Failed to download MP3 file.')
    else:
        print('MP3 file Already downloaded.')


def getReminders(email):


    response = requests.get(REMINDERS_API_URL.format(email))
    output = []

    if response.status_code == 200:
        data = response.json()['data']
        for d in data:
            if d["isEnabled"]:
                download_mp3(d["audioUrl"],"audio/" + d["audioFile"])
                output.append(d)

    else:
        print("FAILED WITH STATUS CODE {}".format(response.status_code))

    return output


def add_subtract_time(time_str, minutes):
    # Convert time string to a datetime object
    time_obj = datetime.strptime(time_str, "%H:%M")

    # Create a timedelta object for the given number of minutes
    time_delta = timedelta(minutes=minutes)

    # Perform addition/subtraction on the time object
    result_time = time_obj + time_delta

    # Format the resulting time as a string
    result_str = result_time.strftime("%H:%M")

    return result_str

