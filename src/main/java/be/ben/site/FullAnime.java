package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.repository.Video;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import be.ben.service.dao.VideoService;
import okio.Options;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
                    System.out.println(href);
                    if(!href.contains("www")){
                        href=href.replace("http://","http://www.");
                    }

                    String title = e.text();
                    Manga mangaFind = mangaService.ifExistTitle(title, "FullAnime");
                    Manga mangaAdd = new Manga();
                    if (mangaFind == null) {
                        mangaAdd.setType("FullAnime");
                        mangaAdd.setTitle(title);
                        mangaAdd.setTitleOriginal(title);
                        mangaService.save(mangaAdd);
                        recupEp(href,title,mangaAdd);

                    }else {
                        mangaAdd=mangaFind;
                        if(mangaAdd.getEpisodes().size()==0){
                            recupEp(href,title,mangaAdd);
                        }
                    }

                } catch (Exception exx) {
                    exx.printStackTrace();
                }


            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void recupEp(String href,String title,Manga mangaAdd){
        HashMap<Integer,String> findEpisode= new HashMap<>();
        Document docEp=null;
        try {
            docEp = Jsoup.connect(href).timeout(60000)
                    .userAgent("Mozilla")
                    .get();
            Elements episodes = docEp.select("div[class=td-block-span6]");
            Optional<Element> elementOptional =episodes.stream().filter((a)->{
                String hrefEp = a.select("a").attr("href");
                int start = hrefEp.indexOf("ode-")+4;
                int end = hrefEp.indexOf("-vos");
                return hrefEp.substring(start,end).chars().allMatch( Character::isDigit );

            }).findFirst();

                          /* List<Element> eps =episodes.stream().filter((a)->{
                               String hrefEp = a.select("a").attr("href");
                               int start = hrefEp.indexOf("ode-")+4;
                               int end = hrefEp.indexOf("-vostfr");
                               return hrefEp.substring(start,end).chars().allMatch( Character::isDigit );
                           }).collect(Collectors.toList());



                            if(elementOptional.isPresent()){

                                String numEp = elementOptional.get().select("a").attr("href");
                                int start = numEp.indexOf("ode-")+4;
                                int end = numEp.indexOf("-vost");
                                String numEpM=numEp.substring(start,end);
                                int nbEp=Integer.parseInt(numEpM);
                                String value = numEp.subSequence(0,start)+String.valueOf(nbEp);
                                value=value+numEp.subSequence(end,numEp.length());
                                findEpisode.put(Integer.valueOf(nbEp),value);
                                eps=eps.subList(1,eps.size());

                                for(Element element : eps){
                                    numEp=element.select("a").attr("href");

                                    start = numEp.indexOf("ode-")+4;
                                   end = numEp.indexOf("-vost");
                                    numEpM=numEp.substring(start,end);
                                    String hrefEp = numEp.subSequence(0,start)+numEpM;
                                    hrefEp=hrefEp+numEp.subSequence(end,numEp.length());
                                    findEpisode.put(Integer.valueOf(numEp),value);
                                }
                            }*/

            if(elementOptional.isPresent()){
                List<Element> eps =episodes.stream().filter((a)->{
                    String hrefEp = a.select("a").attr("href");
                    int start = hrefEp.indexOf("ode-")+4;
                    int end = hrefEp.indexOf("-vos");
                   if(hrefEp.contains("-vos")){
                       return hrefEp.substring(start,end).chars().allMatch( Character::isDigit );
                   }else {
                       return false;
                   }

                }).collect(Collectors.toList());
                String hrefEp="";
                if(eps.size()>1){
                    hrefEp=eps.get(1).select("a").attr("href");
                }else {
                    hrefEp=elementOptional.get().select("a").attr("href");
                }

                String numEp = elementOptional.get().select("a").attr("href");
                int start = numEp.indexOf("ode-")+4;
                int end = numEp.indexOf("-vos");
                String numEpM=numEp.substring(start,end);
                int nbEp=Integer.parseInt(numEpM);
                for(int i=nbEp;i>=1;i--){
                    String numEps="";
                    if(i<10){
                        numEps="0"+String.valueOf(i);
                    }else{
                        numEps=String.valueOf(i);
                    }
                    if(title.equals("Ace Attorney Saison 2")){
                        System.out.println();
                    }
                    if(i==nbEp){
                        String value = hrefEp.subSequence(0,start)+numEps;
                        value=value+numEp.subSequence(end,numEp.length());
                        findEpisode.put(Integer.valueOf(numEps),value);
                    }else {
                        String value = hrefEp.subSequence(0,start)+numEps;
                        value=value+hrefEp.subSequence(end,hrefEp.length());
                        findEpisode.put(Integer.valueOf(numEps),value);
                    }


                }
            }



            if(findEpisode.size()>0){
                System.out.println("ok video");

                if(title.equals("Attack on Titan Saison 1")){
                    System.out.println();
                }
                   episodeAdd(findEpisode,title,mangaAdd);
            }else {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createManga(String url) throws IOException {
    }

    @Override
    public void addVideo(Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getUrl()).timeout(60000).get();
            Elements  urlVideos = doc.select("div[class=td-post-content]").select("iframe");

            for (int j = 0; j < urlVideos.size(); j++) {
                Video video2 = new Video();
                video2.setEpisode(episode);
                String videos = urlVideos.get(j).attr("src");
                video2.setUrl(videos);
                if(videoService.findVideoByUrlAndEpisode(videos,episode)==null){
                    video2.setUrl(videos);
                    videoService.save(video2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }

    public void episodeAdd(HashMap<Integer,String> episodes, String title, Manga mangaAdd) throws IOException {
        episodes.forEach((a,b)->{
            try {
                EpisodeId episodeId = new EpisodeId();
                String url = b;
                episodeId.setNumEp(a.toString());
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
        });
           


    }



    @Scheduled(fixedRate = 3600000,initialDelay =3600000)
    public void updateEpisodes() {
        Document doc;
        Document page;
        try {
            doc = Jsoup.connect("https://www.fullanimevf.co/feed/?rss=2").timeout(60000).get();
            String html = doc.html();
             doc = Jsoup.parse(html, "", Parser.xmlParser());

            Elements items = doc.getElementsByTag("item");
            for(Element item : items){
	try{


                String title =item.select("title").text();
                title=title.split(" Episode")[0];
                String href = item.select("link").get(0).child(0).text();


                Manga m = mangaService.ifExistTitleOriginal(title,"FullAnime");
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
        String numEp =url;
        int start = numEp.indexOf("ode-")+4;
        int end = numEp.indexOf("-vost");
        String numEpM=numEp.substring(start,end);
        episodeId.setNumEp(Integer.valueOf(numEpM).toString());
        episodeId.setTitleManga(title);
        episodeId.setType(mangaAdd.getType());
        Episode episode = new Episode();
        episode.setEpisode_id(episodeId);
        episode.setManga(mangaAdd);
        episode.setUrl(url);
        Episode e = episodeService.findByTitleMangaAndType(title,"FullAnime",numEpM);
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


}
