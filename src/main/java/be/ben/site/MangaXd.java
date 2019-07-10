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
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

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
            doc = Jsoup.connect(url).timeout(60000).get();
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
                    mangaService.save(mangaAdd);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }






            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createManga3(String url) {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {
            doc = Jsoup.connect(url).timeout(60000).get();
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
                    if(mangaAdd.getAnimes()!=null &&mangaAdd.getAnimes().size()>0) {
                        try {
                            Document docc = Jsoup.connect("https://www.mangaxd.com/anime/" + titleRefactor).timeout(60000).get();
                            episodeAdd(docc.select("a[class=episode]"), mangaAdd.getTitle(), mangaAdd, titleRefactor);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void episodeAdd(Elements episodes, String title, Manga mangaAdd, String titleRefractor) {
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
            try {
                addVideo(episode);
            } catch (IOException e) {
                e.printStackTrace();
            }
            j++;
        }
    }

    public void addEpisode(String href , String title, Manga mangaAdd,String ep ) {
            EpisodeId episodeId = new EpisodeId();
            String[] all = ep.split(" ");
            String nep = all[1];
            if (nep.chars().allMatch(Character::isDigit)) {
                double nepInt = Double.parseDouble(nep);
                int nepnep = (int) nepInt;
                nep = String.valueOf(nepnep);
            }

        Episode e =episodeService.findByTitleMangaAndType(title,"MangaXd",nep);
            if(e==null) {
                episodeId.setNumEp(nep);
                episodeId.setTitleManga(title);
                episodeId.setType(mangaAdd.getType());
                Episode episode = new Episode();
                episode.setEpisode_id(episodeId);
                episode.setManga(mangaAdd);
                episode.setUrl(href);
                episodeService.save(episode);
                try {
                    addVideo(episode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

    }

    @Override
    public void addVideo(Episode episode) throws IOException {
        Video video = new Video();
        video.setEpisode(episode);
        Document doc = Jsoup.connect(episode.getUrl()).timeout(60000).get();
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
                doc2 = Jsoup.connect(urlProvider).timeout(60000).get();
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
            doc = Jsoup.connect(url).timeout(60000).get();
            Elements links = doc.select("li[class=page-item]");

            Element nbPage = links.get(1);
            return nbPage.child(0).text();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }



    public void updateEpisodes() {
        Document doc;
        try {
             doc = Jsoup.connect("https://www.mangaxd.com/rss.xml").timeout(60000).get();
            Elements entrys = doc.select("entry");
            for(Element entry : entrys){
                String title = entry.select("title").get(0).text();
                String[] t =title.split("Episode");
                title=t[0];
                String episode = t[1];
                String s = title.replaceAll("&lt;",">").
                        replaceAll("&gt;","<").
                        replaceAll("&#039;","'").
                        replaceAll("&#034;","''").replaceAll("&quot;","\"");
                Manga m = mangaService.ifExistTitleOriginal(s,"MangaXd");
               String href=entry.select("link").attr("href");

              if(m!=null){


                  this.addEpisode(href,s,m,episode);
              }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String addMangaXdEp() throws SQLException {
        int max = Integer.valueOf(getMaxPage("https://www.mangaxd.com/anime"));
        max++;
        for (int i = 1; i < max; i++) {
            createManga3(url + i);
        }

      /*  List<Manga> mangas = this.mangaService.findAllByTypeAndAnimesNotNull("MangaXd");
        for (Manga m:mangas) {
            try {
                String titleRefractor=m.getTitle().substring(30, m.getTitle().length());
                Document docc = Jsoup.connect("https://www.mangaxd.com/anime/" + titleRefractor).timeout(60000).get();
                mangaXd.episodeAdd(docc.select("a[class=episode]"), m.getTitle(), m, titleRefractor);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }*/

        return "start completed  ";
    }

    private void createManga2(String s) {
    }
}
