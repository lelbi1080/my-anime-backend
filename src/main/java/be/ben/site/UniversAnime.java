package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UniversAnime extends Site {

    @Autowired
    private MangaService mangaService;
    private String urlMangas = "https://www.universanimez.com/liste-des-animes";
    private String urlSite = "https://www.universanimez.com";

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


            doc = Jsoup.connect(url).timeout(60000)
                    .userAgent("Mozilla")
                    .get();


            Elements uls = doc.select("ul[class=lcp_catlist]");
            Elements lis = uls.select("li");
            for (Element e : lis.subList(0, lis.size() / 2)) {
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

    public void episodeAdd(Elements episodes, String title, Manga mangaAdd) throws IOException {
        int j = 1;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            EpisodeId episodeId = new EpisodeId();
            String url = episodes.get(i).attr("href");
            if (url != "") {

                String ep = url;
                try {
                    Pattern pattern;
                    if (ep.contains("s2")) {
                        pattern = Pattern.compile("s2-[0-9]+-vost");
                        // ep=ep.replaceAll("s2","");
                    } else if (ep.contains("s3")) {
                        pattern = Pattern.compile("s3-[0-9]+-vost");
                        //  ep=ep.replaceAll("s3","");
                    } else if (ep.contains("s4")) {
                        pattern = Pattern.compile("s4-[0-9]+-vost");
                        //ep= ep.replaceAll("s4","");
                    } else if (ep.contains("oav")) {
                        pattern = Pattern.compile("oav-[0-9]+-vost");
                    } else if (ep.contains("s5")) {
                        pattern = Pattern.compile("s5-[0-9]+-vost");
                        //ep=ep.replaceAll("s5","");
                    } else {
                        pattern = Pattern.compile("-[0-9]+-vost");
                    }


                    Matcher m = pattern.matcher(ep);
                    String test = "";
                    while (m.find()) {
                        test = m.group(0);
                    }
                    ep = test.replaceAll("-", "");
                    ep = ep.replaceAll("vost", "");
                    //System.out.println(ep);
                    if (ep != "") {
                        if (ep.substring(0, 1).equals("0")) {
                            ep = ep.replace("0", "");
                        }
                        episodeId.setNumEp(ep);
                        episodeId.setTitleManga(title);
                        episodeId.setType(mangaAdd.getType());
                        Episode episode = new Episode();
                        episode.setEpisode_id(episodeId);
                        episode.setManga(mangaAdd);
                         if(url.contains("https://www.")){
                            episode.setUrl(url);
                        }else {
                            episode.setUrl(this.urlSite+url);
                        }
                        episodeService.save(episode);
                        addVideo(episode);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            j++;
        }
    }

    public void addEpisode(String url, String title, Manga mangaAdd) throws IOException {
            EpisodeId episodeId = new EpisodeId();
            if (url != "") {

                String ep = url;
                try {
                    Pattern pattern;
                    if (ep.contains("s2")) {
                        pattern = Pattern.compile("s2-[0-9]+-vost");
                        // ep=ep.replaceAll("s2","");
                    } else if (ep.contains("s3")) {
                        pattern = Pattern.compile("s3-[0-9]+-vost");
                        //  ep=ep.replaceAll("s3","");
                    } else if (ep.contains("s4")) {
                        pattern = Pattern.compile("s4-[0-9]+-vost");
                        //ep= ep.replaceAll("s4","");
                    } else if (ep.contains("oav")) {
                        pattern = Pattern.compile("oav-[0-9]+-vost");
                    } else if (ep.contains("s5")) {
                        pattern = Pattern.compile("s5-[0-9]+-vost");
                        //ep=ep.replaceAll("s5","");
                    } else {
                        pattern = Pattern.compile("-[0-9]+-vost");
                    }


                    Matcher m = pattern.matcher(ep);
                    String test = "";
                    while (m.find()) {
                        test = m.group(0);
                    }
                    ep = test.replaceAll("-", "");
                    ep = ep.replaceAll("vost", "");
                    //System.out.println(ep);
                    if (ep != "") {
                        if (ep.substring(0, 1).equals("0")) {
                            ep = ep.replace("0", "");
                        }
                        Episode e =episodeService.findByTitleMangaAndType(title,"UniversAnime",ep);
                        episodeId.setNumEp(ep);
                        episodeId.setTitleManga(title);
                        episodeId.setType(mangaAdd.getType());
                        Episode episode = new Episode();
                        episode.setEpisode_id(episodeId);
                        episode.setManga(mangaAdd);
                        if(url.contains("https://www.")){
                            episode.setUrl(url);
                        }else {
                            episode.setUrl(this.urlSite+url);
                        }

                        if(e==null) {
                            episodeService.save(episode);
                            try {
                                addVideo(episode);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }else {
                        try {
                            addVideo(episode);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

        }
    }
    @Override
    public void addVideo(Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getUrl()).timeout(60000).get();
            Elements elements = doc.select("a[target=iframe_a]");
            for (Element e : elements) {
                String urlVideo = e.attr("href");
                if(videoService.findVideoByUrlAndEpisode(urlVideo,episode)==null){
                    Video video = new Video();
                    video.setEpisode(episode);
                    video.setUrl(urlVideo);
                    videoService.save(video);
                }

            }
            elements = doc.select("iframe");
            for (Element e : elements) {
                String urlVideo = e.attr("src");
                if(videoService.findVideoByUrlAndEpisode(urlVideo,episode)==null){
                    Video video = new Video();
                    video.setEpisode(episode);
                    video.setUrl(urlVideo);
                    videoService.save(video);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }

    @Scheduled(fixedRate = 3600000,initialDelay = 3600000)
    public void updateEpisodes() {
       Document doc;
       Document page;
        try {
            doc = Jsoup.connect("https://www.universanimez.com/feed").timeout(60000).get();
            Elements items = doc.select("item");
            for(Element item : items){
                Element link = item.select("link").get(0);
                page =Jsoup.connect(link.text()).timeout(60000).get();
                Elements iframes = page.select("iframe");
                if(iframes.size()==0){
                    doc = Jsoup.connect(link.text()).userAgent("Mozilla").timeout(60000).get();
                    Elements episodes = doc.select("div.entry-content").select("a");
                    Element episode = episodes.get(0);
                    String title = item.select("title").text();
                    Manga m = mangaService.ifExistTitleOriginal(title,"UniversAnime");
                    String href=link.text();

                    if(m!=null){


                        this.addEpisode(this.urlSite+episode.attr("href"),title,m);
                    }

                }

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void addMangaComp()  {
        try {
            createMangaComp(urlMangas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMangaComp(String url) throws IOException {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect(url).timeout(60000)
                    .userAgent("Mozilla")
                    .get();


            Elements uls = doc.select("ul[class=lcp_catlist]");
            Elements lis = uls.select("li");
            for (Element e : lis.subList(0, lis.size() / 2)) {
                String href = e.select("a").attr("href");
                String title = e.text();
                if (!title.endsWith("VF") && !title.contains("vf") && !title.contains("VF")) {
                    Manga mangaFind = mangaService.ifExistTitle(title, "UniversAnime");
                    Manga mangaAdd = new Manga();
                    if (mangaFind == null) {
                        mangaAdd.setType("UniversAnime");
                        mangaAdd.setTitle(title);
                        mangaAdd.setTitleOriginal(title);
                        mangaService.save(mangaAdd);
                        Document docEp=null;
                        try {
                            docEp = Jsoup.connect(href).userAgent("Mozilla").timeout(60000).get();
                            Elements episodes = docEp.select("div.entry-content").select("a");
                            this.episodeAdd(episodes, title, mangaAdd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }else {
                        mangaAdd=mangaFind;
                        if(mangaAdd.getEpisodes().size()==0){
                            Document docEp=null;
                            try {
                                docEp = Jsoup.connect(href).userAgent("Mozilla").timeout(60000).get();
                                Elements episodes = docEp.select("div.entry-content").select("a");
                                this.episodeAdd(episodes, title, mangaAdd);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }


                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
