package be.ben.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class MangaGenerale {

    @JsonIgnore
    @OneToOne
    Anime anime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String urlImage;

    public MangaGenerale() {
    }

    public MangaGenerale(String title, String urlImage) {
        this.title = title;
        this.urlImage = urlImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
