package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.Manga;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TeleManga extends Site {

    @Autowired
    private MangaService mangaService;
    private String urlMangas = "http://www.telemanga.net/classement.php?lettre=";

    @Override
    @Scheduled(fixedRate = 60000, initialDelay = 0)
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

            String a = "09";
            doc = Jsoup.connect(url + a)
                    .userAgent("Mozilla")
                    .get();

            Elements divs = doc.select("div[class=entry-content clearfix]");

            Element title;
            ;
            Element div;
            Element pageUrl;
            for (int j = 0; j < divs.size(); j++) {
                div = divs.get(j);
                Elements titles = div.select("h2[class=entry-title]");
                Elements pageUrls = div.select("footer[class=entry-meta]");
                title = titles.get(0);
                pageUrl = pageUrls.get(0);
                if (!title.text().endsWith("VF")) {

                    String titleManga = title.text();
                    String urlMangaPage = pageUrl.select("a").attr("href");


                    // System.out.println(link.attr("href"));
                    Manga manga = new Manga();
                    Manga mangaFind = mangaService.ifExistTitle(titleManga, "TeleManga");
                    if (mangaFind != null) {
                        manga = mangaFind;
                    }

                    manga.setType("TeleManga");
                    manga.setTitle(titleManga);
                    manga.setTitleOriginal(titleManga);

                    mangaService.save(manga);
                }
            }

            char alphabet = 'A';

            do {
                doc = Jsoup.connect(url + alphabet)
                        .userAgent("Mozilla")
                        .get();

                divs = doc.select("div[class=entry-content clearfix]");

                for (int j = 0; j < divs.size(); j++) {
                    div = divs.get(j);
                    Elements titles = div.select("h2[class=entry-title]");
                    Elements pageUrls = div.select("footer[class=entry-meta]");
                    title = titles.get(0);
                    pageUrl = pageUrls.get(0);
                    if (!title.text().endsWith("VF")) {

                        String titleManga = title.text();
                        String urlMangaPage = pageUrl.select("a").attr("href");

                        // System.out.println(link.attr("href"));
                        Manga manga = new Manga();
                        Manga mangaFind = mangaService.ifExistTitle(titleManga, "TeleManga");
                        if (mangaFind != null) {
                            manga = mangaFind;
                        }

                        manga.setType("TeleManga");
                        manga.setTitle(titleManga);
                        manga.setTitleOriginal(titleManga);

                        mangaService.save(manga);
                    }
                }


                alphabet++;
            } while (alphabet <= 'Z');


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
