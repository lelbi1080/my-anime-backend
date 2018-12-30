package be.ben.service.dao;

import be.ben.repository.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeService extends JpaRepository<Episode, Integer> {

}
