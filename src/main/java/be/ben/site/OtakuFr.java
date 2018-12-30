package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OtakuFr extends Site {

    @Autowired
    private MangaService mangaService;

    @Autowired
    private EpisodeService episodeService;
    private String urlMangas = "http://www.otakufr.com/anime-list-all/";

    @Override
    public void addManga() {
        try {
            createManga(urlMangas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createManga(String url) throws IOException {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .get();

            Elements boxs = doc.select("div[class=box]");
            Elements uls = boxs.select("ul");
            Elements lis = uls.select("li");
            for (Element e : lis) {
                String href = e.select("a").attr("href");
                String title = e.text();
                Manga mangaFind = mangaService.ifExistTitle(title, "OtakuFr");
                Manga mangaAdd = new Manga();
                if (mangaFind != null) {
                    mangaAdd = mangaFind;
                }

                // mangaAdd.setTitle(link.child(0).text());
                mangaAdd.setType("OtakuFr");
                //System.out.println(link.child(0).text());
                mangaAdd.setTitle(title);
                mangaAdd.setTitleOriginal(title);
                mangaService.save(mangaAdd);
                try {
                    doc = Jsoup.connect(href)
                            .userAgent("Mozilla")
                            .get();
                    Elements episodes = doc.select("ul[class=lst]").select("li");


                    episodeAdd(episodes, title, mangaAdd);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addVideo(Episode episode) {

    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }

    private void episodeAdd(Elements episodes, String title, Manga mangaAdd) throws IOException {
        int j = 1;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            EpisodeId episodeId = new EpisodeId();
            episodeId.setNumEp("");
            episodeId.setTitleManga(title);
            Episode episode = new Episode();
            episode.setEpisode_id(episodeId);
            episode.setManga(mangaAdd);
            episode.setUrl(episodes.get(i).select("a").attr("href"));
            episodeService.save(episode);
            addVideo(episode);
            j++;
        }
    }


}
