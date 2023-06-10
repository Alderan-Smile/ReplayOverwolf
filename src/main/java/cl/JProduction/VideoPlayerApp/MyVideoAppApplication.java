package cl.JProduction.VideoPlayerApp;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/************************************************************************************************************
 * @author Oliver Consterla Araya                                                                           *
 * @version 2023610.0.3                                                                                     *
 * @since 2023                                                                                              *
 ************************************************************************************************************/
public class MyVideoAppApplication {
    private static final String FOLDER_PATH = "C:\\Users\\Admn\\Videos\\Overwolf\\Outplayed";
    private static final String[] IGNORED_FOLDERS = {"temp-capture"};

    public static void main(String[] args) throws IOException, InterruptedException {
        // Crear un objeto que implemente la interfaz WatchService
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Registrar la carpeta para eventos de creación y modificación de archivos
        Path folderPath = Paths.get(FOLDER_PATH);
        folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

        // Crear un hilo para manejar los eventos de la carpeta en segundo plano
        Thread thread = new Thread(() -> {
            try {
                // Monitorear la carpeta en busca de eventos
                while (true) {
                    WatchKey key = watchService.take(); // Esperar por un evento

                    // Procesar los eventos recibidos
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) || kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            // Obtener el nombre del archivo creado o modificado
                            String fileName = event.context().toString();

                            // Verificar si el archivo tiene la extensión .mp4
                            if (fileName.endsWith(".mp4")) {
                                // Ignorar las carpetas específicas
                                boolean isIgnoredFolder = false;
                                for (String ignoredFolder : IGNORED_FOLDERS) {
                                    if (folderPath.resolve(ignoredFolder).toAbsolutePath().startsWith(folderPath.resolve(fileName).toAbsolutePath())) {
                                        isIgnoredFolder = true;
                                        break;
                                    }
                                }

                                if (!isIgnoredFolder) {
                                    // Reproducir el video
                                    playVideo(folderPath.resolve(fileName).toFile());
                                }
                            }
                        }
                    }

                    key.reset(); // Reiniciar el WatchKey para recibir más eventos
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Iniciar el hilo
        thread.start();

        // Mantener la aplicación en ejecución
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static void playVideo(File videoFile) throws IOException {
        // Abrir el archivo de video con el reproductor predeterminado del sistema
        Desktop.getDesktop().open(videoFile);
    }
}