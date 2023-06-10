# ############################################################################################################
#  @author Oliver Consterla Araya                                                                            #
#  @version 202369.21.58                                                                                     #
#  @since 2023                                                                                               #
# ############################################################################################################
import os
import time
from flask import Flask, render_template
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

path_carpeta = "C:/Users/Admn/Videos/Overwolf/Outplayed/"
carpetas_ignoradas = ["CarpetaIgnorada1", "CarpetaIgnorada2"]
server_port = 8000

app = Flask(__name__)
ultimo_video = None

index_html_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "index.html")
print("Ruta del archivo",index_html_path)

class CustomEventHandler(FileSystemEventHandler):
    def __init__(self):
        super().__init__()

    def on_modified(self, event):
        global ultimo_video
        if not event.is_directory and event.src_path.endswith(".mp4"):
            directorio_actual = os.path.dirname(event.src_path)
            if not any(ignorada in directorio_actual for ignorada in carpetas_ignoradas):
                ultimo_video = event.src_path
                print(f"Nuevo video modificado: {ultimo_video}")

@app.route('/')
def index():
    return render_template(index_html_path, ultimo_video=ultimo_video)

if __name__ == "__main__":
    event_handler = CustomEventHandler()
    observer = Observer()
    observer.schedule(event_handler, path_carpeta, recursive=True)
    observer.start()
    print("Observando la carpeta...")

    app.run(port=server_port)

    observer.stop()
    observer.join()