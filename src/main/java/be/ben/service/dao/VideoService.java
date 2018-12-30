package be.ben.service.dao;


import be.ben.repository.Episode;
import be.ben.repository.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoService extends JpaRepository<Video, Integer> {


    public List<Video> getVideoByEpisode(Episode episode);
}

