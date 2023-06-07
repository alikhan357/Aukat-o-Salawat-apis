#
#import pygame
import time
import requests
import os



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


response = requests.get("http://localhost:5000/api/v1/reminder/pi/reminders/{}".format("aliammarkhanbitw@gmail.com"))

if response.status_code == 200:
    data = response.json()['data']
    for d in data:
        if d["isEnabled"]:
            time = d["time"].split(" ")
            print(time[0])
            print(d["audioFile"])
            download_mp3(d["audioUrl"],"audio/" + d["audioFile"])
            print(d["audioUrl"])
else:
    print("FAILED WITH STATUS CODE {}".format(response.status_code))