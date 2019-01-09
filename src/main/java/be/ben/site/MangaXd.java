package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MangaXd extends Site {

    public final String url = "https://www.mangaxd.com/anime?page=";

    public final String urlAnime = "https://www.mangaxd.com/";

    @Autowired
    private MangaService mangaService;

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private VideoService videoService;


    public MangaXd() {
    }

    @Override
    // @Scheduled(fixedRate = 60000,initialDelay = 0)
    public void addManga() {
        int max = Integer.valueOf(getMaxPage("https://www.mangaxd.com/anime"));
        max++;
        for (int i = 1; i < max; i++) {
            createManga(url + i);
        }
    }

    @Override
    public void createManga(String url) {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements links = doc.select("h2[class=name text-center]");
            Element link;

            for (int j = 0; j < links.size(); j++) {
                link = links.get(j);

                String title = link.child(0).attr("href");
                String titleRefactor = title.substring(30, title.length());
                Manga mangaFind = mangaService.ifExistTitle(link.child(0).text(), "MangaXd");
                Manga mangaAdd = new Manga();
                if (mangaFind != null) {
                    mangaAdd = mangaFind;
                }

                // mangaAdd.setTitle(link.child(0).text());
                mangaAdd.setType("MangaXd");
                //System.out.println(link.child(0).text());
                mangaAdd.setTitle(link.child(0).text());
                mangaAdd.setTitleOriginal(link.child(0).text());
                try {
                    Document docc = Jsoup.connect("https://www.mangaxd.com/anime/" + titleRefactor).get();
                    mangaService.save(mangaAdd);
                    episodeAdd(docc.select("a[class=episode]"), link.child(0).text(), mangaAdd, titleRefactor);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }






            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void episodeAdd(Elements episodes, String title, Manga mangaAdd, String titleRefractor) {
        int j = 1;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            EpisodeId episodeId = new EpisodeId();
            String ep = episodes.get(i).text();
            int index = ep.lastIndexOf(' ');
            String nep = ep.substring(++index);
            if (nep.chars().allMatch(Character::isDigit)) {
                double nepInt = Double.parseDouble(nep);
                int nepnep = (int) nepInt;
                nep = String.valueOf(nepnep);
            }

            episodeId.setNumEp(nep);
            episodeId.setTitleManga(title);
            episodeId.setType(mangaAdd.getType());
            Episode episode = new Episode();
            episode.setEpisode_id(episodeId);
            episode.setManga(mangaAdd);
            episode.setUrl(episodes.get(i).attr("href"));
            episodeService.save(episode);
           /* try {
                addVideo(episode);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            j++;
        }
    }

    @Override
    public void addVideo(Episode episode) throws IOException {
        Video video = new Video();
        video.setEpisode(episode);
        Document doc = Jsoup.connect(episode.getUrl()).get();
        Elements elements = doc.select("iframe[class=iframe]");
        String video1 = elements.get(0).attr("src");
        video.setUrl(video1);
        videoService.save(video);
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
            videoService.save(video2);

        }
    }


    @Override
    public String getMaxPage(String url) {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements links = doc.select("li[class=page-item]");

            Element nbPage = links.get(1);
            return nbPage.child(0).text();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
