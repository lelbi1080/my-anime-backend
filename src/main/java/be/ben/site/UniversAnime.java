package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.Manga;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UniversAnime extends Site {

    @Autowired
    private MangaService mangaService;
    private String urlMangas = "https://www.universanimez.com/liste-des-animes";

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


            Elements uls = doc.select("ul[class=lcp_catlist]");
            Elements lis = uls.select("li");
            for (Element e : lis) {
                String href = e.select("a").attr("href");
                String title = e.text();
                if (!title.endsWith("VF") && !title.contains("vf") && !title.contains("VF")) {
                    Manga mangaFind = mangaService.ifExistTitle(title, "UniversAnime");
                    Manga mangaAdd = new Manga();
                    if (mangaFind != null) {
                        mangaAdd = mangaFind;
                    }

                    // mangaAdd.setTitle(link.child(0).text());
                    mangaAdd.setType("UniversAnime");
                    //System.out.println(link.child(0).text());
                    mangaAdd.setTitle(title);
                    mangaAdd.setTitleOriginal(title);
                    mangaService.save(mangaAdd);
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


}
