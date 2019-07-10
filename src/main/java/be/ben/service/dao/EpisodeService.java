package be.ben.service.dao;

import be.ben.repository.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EpisodeService extends JpaRepository<Episode, Integer> {
    @Query("select e from Episode e where e.episode_id.numEp=:numEp and e.manga.title=:manga_title and e.episode_id.type=:type")
    List<Episode> findByNumEpAndManga_Title(String numEp, String manga_title, String type);
    @Query("select e from Episode e where e.episode_id.titleManga=:title and e.episode_id.type=:type and e.episode_id.numEp=:ep")
    Episode findByTitleMangaAndType(String title,String type,String ep);
}
