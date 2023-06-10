package cl.JProduction.VideoPlayerApp.model;

import lombok.Data;

import java.time.LocalDateTime;

/************************************************************************************************************
 * @author Oliver Consterla Araya                                                                           *
 * @version 202369.23.53                                                                                    *
 * @since 2023                                                                                              *
 ************************************************************************************************************/
@Data
public class Video {
    private String name;
    private LocalDateTime lastModified;

    public Video(String name, LocalDateTime lastModified) {
        this.name = name;
        this.lastModified = lastModified;
    }
}
