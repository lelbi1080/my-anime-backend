package be.ben.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    private String titleOriginal;
    private String type;
    @JsonIgnore
    @OneToMany(mappedBy = "manga")
    private List<Episode> episodes;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "test_a",
            joinColumns = {@JoinColumn(name = "manga_id")},
            inverseJoinColumns = {@JoinColumn(name = "anime_id")})
    @JsonIgnore
    private List<Anime> animes = new ArrayList<>();

    public Manga() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public List<Anime> getManga() {
        return animes;
    }

    public void setManga(Anime manga) {
        this.animes.add(manga);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getTitleOriginal() {
        return titleOriginal;
    }

    public void setTitleOriginal(String titleOriginal) {
        this.titleOriginal = titleOriginal;
    }

    public List<Anime> getAnimes() {
        return animes;
    }

    public void setAnimes(List<Anime> animes) {
        this.animes = animes;
    }
}
