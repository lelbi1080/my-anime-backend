package be.ben.repository;


import javax.persistence.*;
import java.util.List;

@Entity
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne
    private MangaGenerale mangaGenerale;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "animes")
    private List<Manga> mangaList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MangaGenerale getMangaGenerale() {
        return mangaGenerale;
    }

    public void setMangaGenerale(MangaGenerale mangaGenerale) {
        this.mangaGenerale = mangaGenerale;
    }

    public List<Manga> getMangaList() {
        return mangaList;
    }

    public void setMangaList(List<Manga> mangaList) {
        this.mangaList = mangaList;
    }
}
