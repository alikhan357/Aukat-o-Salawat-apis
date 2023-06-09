
import os
import requests
import time
from datetime import datetime, timedelta
import os
import logging
from logging.handlers import TimedRotatingFileHandler
import subprocess
from subprocess import PIPE

def play(audio):
    #soundplay(audio,1)
    #time.sleep(600000)
    P = subprocess.Popen("ffplay -nodisp -autoexit -loglevel quiet {}".format(audio), universal_newlines=True,shell=True, stdout=PIPE, stderr=PIPE).communicate()
   
  



def download_mp3(url, save_path):

    logger.info("downloading file {}".format(url))

    if not os.path.exists(save_path):
        response = requests.get(url)
        if response.status_code == 200:
            with open(save_path, 'wb') as file:
                file.write(response.content)
            logger.info('MP3 file downloaded and saved successfully.')
        else:
            logger.info('Failed to download MP3 file.')
    else:
        logger.info('MP3 file Already downloaded.')


def getReminders():

    response = requests.get(REMINDERS_API_URL.format(serial_number))
    output = []

    if response.status_code == 200:
        data = response.json()['data']
        for d in data:
            if d["isEnabled"] and d["type"] == 'local':
                download_mp3(d["audioUrl"],AUDIO_PATH + d["audioFile"])
            output.append(d)

    else:
        logger.info("FAILED WITH STATUS CODE {}".format(response.status_code))

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

def getserial():
  # Extract serial from cpuinfo file
  cpuserial = "0000000000000000"
  try:
    f = open('/proc/cpuinfo','r')
    for line in f:
      if line[0:6]=='Serial':
        cpuserial = line[10:26]
    f.close()
  except:
    cpuserial = "ERROR000000000"
 
  return cpuserial


REMINDERS_API_URL = "http://aukat-o-salawat-api.ap-northeast-1.elasticbeanstalk.com" + "/api/v1/reminder/pi/reminders/{}"
serial_number = getserial()

logger = logging.getLogger()
logger.setLevel(logging.INFO)
logname = "app_{}.log".format(serial_number)
handler = TimedRotatingFileHandler(logname, when="midnight", backupCount=30)
#handler.suffix = "%Y%m%d"
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
handler.setLevel(logging.INFO)
logger.addHandler(handler)

# logging.basicConfig(filename=logname,
#                     format='%(asctime)s %(message)s',
#                     filemode='w')
 
AUDIO_PATH = "/home/pi/Script/audio/"
