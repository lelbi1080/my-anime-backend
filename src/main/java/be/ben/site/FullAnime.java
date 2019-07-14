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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class FullAnime extends Site {

    @Autowired
    private MangaService mangaService;

    @Autowired
    private EpisodeService episodeService;
    private String urlMangas = "https://www.fullanimevf.co/anime-liste/";

    @Autowired
    private VideoService videoService;

    @Override
    @Scheduled(fixedRate = 3600000,initialDelay =3000)
    public void addManga() {
        try {
            createMangaComp(urlMangas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createMangaComp(String url) throws IOException{

           Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect(url).timeout(60000)
                    .userAgent("Mozilla")
                    .get();
            Elements lis = doc.select("li");
            List<Element> list=lis.subList(6,(lis.size()/2)-1);
            list.forEach((e)->{
                try{
                    String href = e.select("a").attr("href");
                    String title = e.text();
                    System.out.println();
                    Manga mangaFind = mangaService.ifExistTitle(title, "FullAnime");
                    Manga mangaAdd = new Manga();
                    if (mangaFind == null) {
                        mangaAdd.setType("FullAnime");
                        mangaAdd.setTitle(title);
                        mangaAdd.setTitleOriginal(title);
                      //  mangaService.save(mangaAdd);
                        Document docEp=null;
                        try {
                            docEp = Jsoup.connect(href).timeout(60000)
                                    .userAgent("Mozilla")
                                    .get();
                            Elements episodes = docEp.select("div[class=td-block-row]").select("div[class=td-module-thumb]");
                            System.out.println(episodes.get(0).html());
                           // Elements episodes = docEp.select("ul[class=lst]").select("li");
                            //this.episodeAdd(episodes, title, mangaAdd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } catch (Exception exx) {
                    exx.printStackTrace();
                }


            });

        } catch (IOException e) {
            e.printStackTrace();
        }
       /* Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect(url).timeout(60000)
                    .userAgent("Mozilla")
                    .get();

            Elements boxs = doc.select("div[class=box]");
            Elements uls = boxs.select("ul");
            Elements lis = uls.select("li");
            for (Element e : lis) {
                try{
                    String href = e.select("a").attr("href");
                    String title = e.text();
                    Manga mangaFind = mangaService.ifExistTitle(title, "OtakuFr");
                    Manga mangaAdd = new Manga();
                    if (mangaFind == null) {
                        mangaAdd.setType("OtakuFr");
                        mangaAdd.setTitle(title);
                        mangaAdd.setTitleOriginal(title);
                        mangaService.save(mangaAdd);
                        Document docEp=null;
                        try {
                            docEp = Jsoup.connect(href).timeout(60000)
                                    .userAgent("Mozilla")
                                    .get();
                            Elements episodes = docEp.select("ul[class=lst]").select("li");
                            this.episodeAdd(episodes, title, mangaAdd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } catch (Exception exx) {
                    exx.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void createManga(String url) throws IOException {
      /*  Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect(url).timeout(60000)
                    .userAgent("Mozilla")
                    .get();
            Elements lis = doc.select("li");
            List<Element> list=lis.subList(6,(lis.size()/2)-1);
            list.forEach((e)->{
                System.out.println(e.html());
            });

          /*  Elements boxs = doc.select("div[class=box]");
            Elements uls = boxs.select("ul");
            Elements lis = uls.select("li");
            for (Element e : lis) {
	try{
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
  	} catch (Exception exx) {
                exx.printStackTrace();
            }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void addVideo(Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getUrl()).timeout(60000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements providers = doc.select("div[class=nav_ver]").select("a");

        String urlProvider;
        for (int j = 0; j < providers.size(); j++) {
            Video video2 = new Video();
            video2.setEpisode(episode);
            urlProvider = providers.get(j).attr("href");
            Document doc2 = null;
            try {
                doc2 = Jsoup.connect(urlProvider).timeout(60000).get();
                Elements links = doc2.select("div[class=vdo_wrp]").select("iframe");
                String videos = links.get(0).attr("src");
                video2.setUrl(videos);
                videoService.save(video2);
            } catch (Exception e) {
                //e.printStackTrace();
            }


        }
    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }

    public void episodeAdd(Elements episodes, String title, Manga mangaAdd) throws IOException {
        int j = 1;
        for (int i = episodes.size() - 1; i >= 0; i--) {
           
            try {
	 EpisodeId episodeId = new EpisodeId();
            String url = episodes.get(i).select("a").attr("href");
            String url2 = url.substring(0, url.length() - 1);
            int x = url2.lastIndexOf("/");
            String ep = "";
            ep = url2.substring(++x, url.length() - 1);
            if (ep.substring(0, 1).equals("0")) {
                ep = ep.replace("0", "");
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
            }catch (Exception ex){
                ex.printStackTrace();
            }
            j++;
        }
    }


    public void updateEpisodes() {
        Document doc;
        Document page;
        try {
            doc = Jsoup.connect("http://www.otakufr.com/anime-rss").timeout(60000).get();
            Elements items = doc.select("item");
            for(Element item : items){
	try{
                Element link = item.select("link").get(0);
                page =Jsoup.connect(link.text()).timeout(60000).get();
                Elements uis= page.select("ul.breadcrumb").select("li");
                String title =uis.get(1).text();
                Manga m = mangaService.ifExistTitleOriginal(title,"OtakuFr");
                String href=link.text();

                if(m!=null){


                    this.addEpisode(href,title,m);
                }
   	}catch (Exception exx){
                exx.printStackTrace();
            }




            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void addEpisode(String url, String title, Manga mangaAdd) throws IOException {
            EpisodeId episodeId = new EpisodeId();
            String url2 = url.substring(0, url.length() - 1);
            int x = url2.lastIndexOf("/");
            String ep = "";
            ep = url2.substring(++x, url.length() - 1);
            if (ep.substring(0, 1).equals("0")) {
                ep = ep.replace("0", "");
            }
        Episode e =episodeService.findByTitleMangaAndType(title,"OtakuFr",ep);
        if(e==null) {
            episodeId.setNumEp(ep);
            episodeId.setTitleManga(title);
            episodeId.setType(mangaAdd.getType());
            Episode episode = new Episode();
            episode.setEpisode_id(episodeId);
            episode.setManga(mangaAdd);
            episode.setUrl(url);
            episodeService.save(episode);
            try {
                addVideo(episode);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
