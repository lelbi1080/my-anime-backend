package be.ben.site;


import be.ben.repository.Episode;

import java.io.IOException;

public abstract class Site {


    protected String patternUrlManga;


    public Site() {
    }

    public abstract void addManga();

    public abstract void createManga(String url) throws IOException;

    public abstract void addVideo(Episode episode) throws IOException;

    public abstract String getMaxPage(String url);
}
