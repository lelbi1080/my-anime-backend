package be.ben.service.dao;

import be.ben.repository.AnimeList;
import be.ben.repository.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnimeListService extends JpaRepository<AnimeList, Integer> {

    List<AnimeList> findByUser(Integer id);

    @Transactional
    @Modifying
    @Query(value = "update AnimeList  SET episode = :episode where user_id=:userId and manga_id=:mangaId"
    ,nativeQuery = true)
    void updateEpisode(int userId,int mangaId,String episode);
}
