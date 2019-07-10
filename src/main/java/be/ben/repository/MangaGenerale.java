package be.ben.repository;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MangaGenerale implements Serializable {

    @JsonIgnore
    @OneToOne
    Anime anime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String urlImage;
    private String urlPage;

    @Column(name = "genres")
    @ElementCollection(targetClass = String.class)
    private List<String> genres;


  /*  @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.ALL
            })
    @JoinTable(name = "mangas_users",
            joinColumns = {@JoinColumn(name = "manga_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users=new ArrayList<>() ;*/

    @OneToMany(
            mappedBy = "manga",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AnimeList> animeListList = new ArrayList<>();

    public List<AnimeList> getAnimeListList() {
        return animeListList;
    }

    public void setAnimeListList(List<AnimeList> animeListList) {
        this.animeListList = animeListList;
    }

    public MangaGenerale() {
    }

    public MangaGenerale(int id, String title, String urlImage) {
        this.id = id;
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getUrlPage() {
        return urlPage;
    }

    public void setUrlPage(String urlPage) {
        this.urlPage = urlPage;
    }

   /* public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }*/
}
