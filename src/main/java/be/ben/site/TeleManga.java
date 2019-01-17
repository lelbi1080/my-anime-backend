package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TeleManga extends Site {


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
            doc = Jsoup.connect(url + a).timeout(60000)
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
                    try {
                        doc = Jsoup.connect(urlMangaPage).timeout(60000)
                                .userAgent("Mozilla")
                                .get();
                        Elements episodes = doc.select("div[class=entry-content]").select("a");


                        episodeAdd(episodes, titleManga, manga);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            char alphabet = 'A';

            do {
                doc = Jsoup.connect(url + alphabet).timeout(60000)
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
                        try {
                            doc = Jsoup.connect(urlMangaPage).timeout(60000)
                                    .userAgent("Mozilla")
                                    .get();
                            Elements episodes = doc.select("div[class=entry-content]").select("a");


                            episodeAdd(episodes, titleManga, manga);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


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


        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getUrl()).timeout(60000).get();
            Elements elements = doc.select("a[target=_blank]");
            for (Element e : elements) {
                String urlVideo = e.attr("href");
                Video video = new Video();
                video.setEpisode(episode);
                video.setUrl(urlVideo);
                videoService.save(video);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

       /*
        Elements providers = doc.select("a[class=provider]");
        String urlProvider;
        for (int j = 0; j < providers.size(); j++) {
            Video video2 = new Video();
            video2.setEpisode(episode);
            urlProvider = providers.get(j).attr("href");
            Document doc2 = null;
            try {
                doc2 = Jsoup.connect(urlProvider).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements links = doc2.select("iframe[class=iframe]");
            String videos = links.get(0).attr("src");
            video2.setUrl(videos);
            videoService.save(video2);*/
    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }


    @Override
    public String getMaxPage(String url) {
        return null;
    }

    private void episodeAdd(Elements episodes, String title, Manga mangaAdd) throws IOException {
        int j = 1;
        for (int i = 2; i < episodes.size(); i++) {
            EpisodeId episodeId = new EpisodeId();
            String url = episodes.get(i).attr("href");
            if (url != "") {


                String url2 = url;
                int ept = url.indexOf("/ep-") + 4;
                int end = url.indexOf("-vost");
                String ep = "";
                try {
                    ep = url.substring(ept, end);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                episodeId.setNumEp(ep);
                episodeId.setTitleManga(title);
                episodeId.setType(mangaAdd.getType());
                Episode episode = new Episode();
                episode.setEpisode_id(episodeId);
                episode.setManga(mangaAdd);
                episode.setUrl(url);
                episodeService.save(episode);
                addVideo(episode);
            }
            j++;
        }
    }


}
