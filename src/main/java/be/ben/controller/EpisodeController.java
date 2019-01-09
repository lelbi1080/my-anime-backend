package be.ben.controller;

import be.ben.repository.Anime;
import be.ben.repository.Episode;
import be.ben.repository.Manga;
import be.ben.service.dao.AnimeService;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import be.ben.util.Episodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class EpisodeController {
    @Autowired
    private be.ben.site.MangaXd mangaXd;

    @Autowired
    private MangaService mangaService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private AnimeService animeService;

    @CrossOrigin
    @RequestMapping("/episodes/{titleGenerale}")
    public List<String> getTitleByb(@PathVariable String titleGenerale) throws IOException {
        Set<String> episodesNumber = new TreeSet<>();
        long id = animeService.findByTitleGenerale(titleGenerale);
        Optional<Anime> a = animeService.findById((int) id);
        List<Episode> episodes = new ArrayList<>();
        for (Manga m : a.get().getMangaList()) {
            episodes.addAll(m.getEpisodes());
        }
        for (Episode e : episodes) {
            episodesNumber.add(e.getEpisode_id().getNumEp());
        }
        List<String> list = new ArrayList<String>(episodesNumber);

        return list;

    }

    @CrossOrigin
    @RequestMapping("/episodesValue/{titleGenerale}")
    public Episodes getEpisodeValue(@PathVariable String titleGenerale) throws IOException {
        Set<String> episodesNumber = new TreeSet<>();
        long id = animeService.findByTitleGenerale(titleGenerale);
        Optional<Anime> a = animeService.findById((int) id);
        List<Episode> episodes = new ArrayList<>();
        for (Manga m : a.get().getMangaList()) {
            episodes.addAll(m.getEpisodes());
        }
        for (Episode e : episodes) {
            episodesNumber.add(e.getEpisode_id().getNumEp());
        }
        List<String> list = new ArrayList<String>(episodesNumber);
        Episodes episodesVal = new Episodes(list);

        return episodesVal;

    }

}
