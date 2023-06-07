import schedule
import time
from helper import getReminders, play
from pytz import timezone

# Dictionary to store reminder times for each namaz
reminders_times = {}

# User email and default timezone
email = "aliammarkhanbitw@gmail.com"
default_timezone = "Asia/Calcutta"

# Path format for audio files
audio_path = "audio/{}"

# Function to execute the reminder job
def job(audio):
    print("Reminder executed")
    play(audio=audio)

# Function to update reminders
def updateReminders():
    # Fetch reminders
    print("Updating reminders")
    reminders = getReminders(email=email)

    if len(reminders) > 0:
        for reminder in reminders:
            time = reminder["time"].split(" ")[0]
            # Check if reminder needs to be updated
            if not (reminder["namaz"] in reminders_times and reminders_times[reminder["namaz"]] == time):
                # Clear any existing reminder for the namaz
                schedule.clear(reminder["namaz"])
                # Schedule a new reminder at the specified time in the default timezone
                schedule.every().day.at(time, timezone(default_timezone)).do(
                    job, audio=audio_path.format(reminder["audioFile"])
                ).tag(reminder["namaz"])
                reminders_times[reminder["namaz"]] = time
                print("{} reminder updated".format(reminder["namaz"]))
            else:
                print("{} reminder already updated".format(reminder["namaz"]))
    else:
        print("No reminders found")

# Main execution
if __name__ == "__main__":
    # Schedule a job to run every 1800 seconds (30 minutes)
    #schedule.every(1800).seconds.do(updateReminders)
    updateReminders()

    while True:
        schedule.run_pending()
        time.sleep(1)