package cl.JProduction.VideoPlayerApp.controller;

import cl.JProduction.VideoPlayerApp.model.Video;
import cl.JProduction.VideoPlayerApp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/************************************************************************************************************
 * @author Oliver Consterla Araya                                                                           *
 * @version 202369.23.53                                                                                    *
 * @since 2023                                                                                              *
 ************************************************************************************************************/
@Controller
public class VideoController {
    private VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Video latestVideo = videoService.getLatestVideo();
        model.addAttribute("latestVideo", latestVideo);
        return "index";
    }
}
