# ############################################################################################################
#  @author Oliver Consterla Araya                                                                            #
#  @version 202369.19.20                                                                                     #
#  @since 2023                                                                                               #
# ############################################################################################################
from controller.fileWatcher import *
import time
from watchdog.observers import Observer
from watchdog.events import PatternMatchingEventHandler

pathCarpeta = "C:/Users/Admn/Videos/Overwolf/Outplayed/"

if __name__ == "__main__":
    print("Inicia if main")
    patterns = ["*.mp4"]
    ignore_patterns = ["./temp-capture"]
    ignore_directories = True
    case_sensitive = True
    my_event_handler = PatternMatchingEventHandler(patterns, ignore_patterns, ignore_directories, case_sensitive)
    print("termina if main")

def on_created(event):
    print(f"hey, {event.src_path} has been created!")
def on_deleted(event):
    print(f"what the f**k! Someone deleted {event.src_path}!")
def on_modified(event):
    print(f"hey buddy, {event.src_path} has been modified")
def on_moved(event):
    print(f"ok ok ok, someone moved {event.src_path} to {event.dest_path}")

my_event_handler.on_created = on_created
my_event_handler.on_deleted = on_deleted
my_event_handler.on_modified = on_modified
my_event_handler.on_moved = on_moved

go_recursively = True
my_observer = Observer()
my_observer.schedule(my_event_handler, pathCarpeta, recursive=go_recursively)
print("Termina definicion de observador")

my_observer.start()
try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    my_observer.stop()
    my_observer.join()