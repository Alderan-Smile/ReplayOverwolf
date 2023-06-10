package cl.JProduction.VideoPlayerApp.service;

import cl.JProduction.VideoPlayerApp.model.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/************************************************************************************************************
 * @author Oliver Consterla Araya                                                                           *
 * @version 202369.23.53                                                                                    *
 * @since 2023                                                                                              *
 ************************************************************************************************************/
@Service
public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);
    @Value("${video.folder.path}")
    private String videoFolderPath;
    @Value("${video.ignored.folders}")
    private List<String> ignoredFolders;
    private List<Video> videos;
    @PostConstruct
    public void init() {
        videos = new ArrayList<>();
        scheduleVideoDetection();
    }
    private void scheduleVideoDetection() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::detectNewVideos, 0, 1, TimeUnit.MINUTES);
    }
    private void detectNewVideos() {
        LOGGER.info("Detecting new videos...");
        File folder = new File(videoFolderPath);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            return;
        }

        List<File> newVideoFiles = Arrays.stream(files)
                .filter(file -> file.isFile() &&
                        file.getName().toLowerCase().endsWith(".mp4") &&
                        !ignoredFolders.contains(file.getParentFile().getName()) &&
                        isNewVideo(file))
                .collect(Collectors.toList());

        if (!newVideoFiles.isEmpty()) {
            LOGGER.info("New videos detected: {}", newVideoFiles.size());
            for (File newVideoFile : newVideoFiles) {
                String videoName = newVideoFile.getName();
                LocalDateTime lastModified = getLastModifiedTime(newVideoFile);
                Video newVideo = new Video(videoName, lastModified);
                videos.add(newVideo);
            }
        }
    }
    private boolean isNewVideo(File file) {
        //LocalDateTime lastModified = LocalDateTime.ofInstant(file.lastModified().toInstant(), ZoneId.systemDefault());
        LocalDateTime lastModified = getLastModifiedTime(file);
        for (Video video : videos) {
            if (video.getName().equals(file.getName()) && video.getLastModified().equals(lastModified)) {
                return false;
            }
        }
        return true;
    }
    private LocalDateTime getLastModifiedTime(File file) {
        long lastModifiedMillis = file.lastModified();
        Instant instant = Instant.ofEpochMilli(lastModifiedMillis);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    public VideoService(@Value("${video.folder.path}") String videoFolderPath,
                        @Value("${video.ignored.folders}") List<String> ignoredFolders) {
        this.videoFolderPath = videoFolderPath;
        this.ignoredFolders = ignoredFolders;
    }

    public Video getLatestVideo() {
        File folder = new File(videoFolderPath);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            return null;
        }

        List<File> filteredFiles = Arrays.stream(files)
                .filter(file -> file.isFile() &&
                        file.getName().toLowerCase().endsWith(".mp4") && // Solo archivos MP4
                        !ignoredFolders.contains(file.getParentFile().getName()))
                .collect(Collectors.toList());

        if (filteredFiles.isEmpty()) {
            return null;
        }

        File latestFile = filteredFiles.stream()
                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .orElse(null);

        if (latestFile != null) {
            String videoName = latestFile.getName();
            LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(latestFile.lastModified()), ZoneId.systemDefault());
            return new Video(videoName, lastModified);
        }

        return null;
    }
}
