package be.ben.repository;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Episode {

    @EmbeddedId
    private EpisodeId episode_id;

    @OneToMany(mappedBy = "episode")
    private List<Video> videos;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "manga_idddd")
    private Manga manga;

    private String url;


    public Episode() {
    }

    public EpisodeId getEpisode_id() {
        return episode_id;
    }

    public void setEpisode_id(EpisodeId episode_id) {
        this.episode_id = episode_id;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

