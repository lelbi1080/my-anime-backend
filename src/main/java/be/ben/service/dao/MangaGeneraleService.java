package be.ben.service.dao;

import be.ben.repository.MangaGenerale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MangaGeneraleService extends JpaRepository<MangaGenerale, Integer> {
    public MangaGenerale findByTitle(String title);

    public List<MangaGenerale> findByTitleStartsWith(char car);

    @Query("Select distinct title from MangaGenerale where anime is not null")
    public List<String> getAllTitleMapped();

    @Query("select new MangaGenerale(id,title,urlImage)   from MangaGenerale where anime_id is not null and title" +
            " like concat(:first,'%') ")
    public List<MangaGenerale> findByAnimeNotNull(String first);


    public MangaGenerale getMangaGeneraleByTitle(String title);


    MangaGenerale findMangaGeneraleById(int id);
}
