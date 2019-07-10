package be.ben.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class AnimeList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private MangaGenerale manga;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private String episode;

    public AnimeList(MangaGenerale manga, User user, String episode) {
        this.manga = manga;
        this.user = user;
        this.episode = episode;
    }

    public AnimeList() {
    }

    public MangaGenerale getManga() {
        return manga;
    }

    public void setManga(MangaGenerale manga) {
        this.manga = manga;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }
}
