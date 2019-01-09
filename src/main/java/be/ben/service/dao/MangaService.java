package be.ben.service.dao;

import be.ben.repository.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MangaService extends JpaRepository<Manga, Integer> {
    public Manga findByTitle(String title);

    public List<Manga> findAllByType(String type);

    @Query("select m from Manga m where m.title =?1  and m.type = ?2")
    public Manga ifExistTitle(String title, String type);


    @Query("select m from Manga m where m.id>= ?1 and m.id < ?2")
    public List<Manga> getBetweenStartToEnd(int start, int end);

    @Query("SELECT m FROM Manga m WHERE m.title LIKE CONCAT(:title,'%')")
    List<Manga> findMangaWithPartOfTitle(@Param("title") String title);


}
