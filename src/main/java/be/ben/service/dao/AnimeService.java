package be.ben.service.dao;

import be.ben.repository.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnimeService extends JpaRepository<Anime, Integer> {

    @Query("select m.anime.id from Anime a join  MangaGenerale m on a.mangaGenerale.id=m.id where m.title=:title")
    public int findByTitleGenerale(String title);

    public Anime findAnimeById(int id);

}
