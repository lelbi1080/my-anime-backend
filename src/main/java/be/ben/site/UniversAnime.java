package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                    doc = Jsoup.connect(href).userAgent("Mozilla").get();
                    Elements episodes = doc.select("div.entry-content").select("a");
                    episodeAdd(episodes, title, mangaAdd);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void episodeAdd(Elements episodes, String title, Manga mangaAdd) throws IOException {
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
                        Episode episode = new Episode();
                        episode.setEpisode_id(episodeId);
                        episode.setManga(mangaAdd);
                        episode.setUrl(url);
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
