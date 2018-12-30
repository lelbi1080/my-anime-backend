package be.ben.controller;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class VideoController {
    @Autowired
    private be.ben.site.MangaXd mangaXd;

    @Autowired
    private MangaService mangaService;

    @Autowired
    private VideoService videoService;


    @CrossOrigin
    @RequestMapping("/videosDb")
    public List<Video> getVideoDb() throws IOException {
        Optional<Manga> manga = mangaService.findById(1);
        EpisodeId episodeId = new EpisodeId();
        episodeId.setTitleManga(manga.get().getTitle());
        episodeId.setNumEp("");
        Episode episode = new Episode();
        episode.setEpisode_id(episodeId);
        return videoService.getVideoByEpisode(episode);


    }

}
