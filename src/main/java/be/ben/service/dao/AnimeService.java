package be.ben.service.dao;

import be.ben.repository.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimeService extends JpaRepository<Anime, Integer> {


}
