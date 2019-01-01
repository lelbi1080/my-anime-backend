package be.ben.site;


import be.ben.repository.Episode;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public abstract class Site {
    @Autowired
    protected MangaService mangaService;

    @Autowired
    protected EpisodeService episodeService;

    @Autowired
    protected VideoService videoService;


    protected String patternUrlManga;


    public Site() {
    }

    public abstract void addManga();

    public abstract void createManga(String url) throws IOException;

    public abstract void addVideo(Episode episode) throws IOException;

    public abstract String getMaxPage(String url);
}
