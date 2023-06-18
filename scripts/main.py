import schedule
import time
from helper import getReminders, play,add_subtract_time,logger,AUDIO_PATH
from pytz import timezone

# Dictionary to store reminder times for each namaz
reminders_times = {}


# Path format for audio files
audio_path = AUDIO_PATH + "{}"

# Function to execute the reminder job
def job(params):
    try:
    
        logger.info("Reminder executed for {}".format(params["namaz_name"]))
        logger.info("Playing file {}".format(params["audio"]))
        play(audio=params["audio"])
        logger.info("Reminder Completed")
    
    except Exception as err:
        logger.error("Exception occured {}".format(err))

# Function to update reminders
def updateReminders():
    # Fetch reminders
    try:
        logger.info("Updating reminders")
        reminders = getReminders()

        if len(reminders) > 0:
            for reminder in reminders:
                time = add_subtract_time(reminder["time"].split(" ")[0],reminder["adjustedTime"])
                # Check if reminder needs to be updated
                if not (reminder["namaz"] in reminders_times and reminders_times[reminder["namaz"]] == time):
                    # Clear any existing reminder for the namaz
                    schedule.clear(reminder["namaz"])
                    # Schedule a new reminder at the specified time in the default timezone
                    schedule.every().day.at(time, timezone(reminder["timeZone"])).do(
                        job, {"audio" : audio_path.format(reminder["audioFile"]), "namaz_name":reminder["namaz"]}
                    ).tag(reminder["namaz"])
                    reminders_times[reminder["namaz"]] = time
                    
                    logger.info("{} reminder updated.".format(reminder["namaz"]))
                    logger.info("Time :{}".format(time))
                    logger.info("TimeZone :{}".format(reminder["timeZone"]))
                
                else:
                    logger.info("{} reminder already updated".format(reminder["namaz"]))
        else:
            logger.info("No reminders found")
    
    except Exception as err:
        logger.error("Exception occured {}".format(err))

# Main execution
if __name__ == "__main__":
    # Schedule a job to run every 7200000 seconds (2 hours)
    schedule.every(7200000).seconds.do(updateReminders)
    updateReminders()

    while True:
        schedule.run_pending()
        time.sleep(1)
