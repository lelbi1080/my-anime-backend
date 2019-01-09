package be.ben.controller;

import be.ben.repository.Anime;
import be.ben.repository.Episode;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import be.ben.service.dao.AnimeService;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VideoController {
    @Autowired
    private be.ben.site.MangaXd mangaXd;

    @Autowired
    private MangaService mangaService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private AnimeService animeService;

    @Autowired
    private EpisodeService episodeService;


    @CrossOrigin
    @RequestMapping("/videos/{titleGenerale}/{numEp}")
    public List<Video> getVideoDb(@PathVariable String titleGenerale, @PathVariable String numEp) throws IOException {
        int id = animeService.findByTitleGenerale(titleGenerale);
        Anime a = animeService.findAnimeById(id);
        List<Video> videos = new ArrayList<>();
        for (Manga m : a.getMangaList()) {
            for (Episode e : episodeService.findByNumEpAndManga_Title(numEp, m.getTitle(), m.getType())) {
                videos.addAll(e.getVideos());
            }
            //videos.addAll(episodeService.f)
        }
        return videos;


    }

}
