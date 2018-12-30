package be.ben.repository;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @JsonIgnore
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "numEp", insertable = true, updatable = true),
            @JoinColumn(name = "titleManga", insertable = true, updatable = true)
    })
    private Episode episode;

    private String url;

    public Video() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
