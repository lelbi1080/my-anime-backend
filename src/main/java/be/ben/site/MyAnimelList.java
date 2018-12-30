package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.MangaGenerale;
import be.ben.service.dao.MangaGeneraleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyAnimelList extends Site {

    @Autowired
    private MangaGeneraleService mangaGeneraleService;
    private String urlMangas = "https://myanimelist.net/anime.php";

    @Override
    public void addManga() {
        createManga(urlMangas);
    }

    @Override
    public void createManga(String url) {
        Document doc = null;

        Document pageDoc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements honriz = doc.select("div#horiznav_nav");
            Elements letters = honriz.select("li");
            Element link;
            String href;
            for (int j = 2; j < letters.size(); j++) {
                link = letters.get(j);
                href = link.child(0).attr("href");
                doc = Jsoup.connect(href).get();
                Elements imgs = doc.select("img[class=lazyload]");
                List<String> images = new ArrayList<>();

                for (Element img : imgs) {

                    String str = img.attr("data-src");
                    str = str.replace("r/50x70/", "");

                    images.add(str);
                }
                Elements titles = doc.select("a[class=hoverinfo_trigger fw-b fl-l]");
                for (int i = 0; i < titles.size(); i++) {
                    //System.out.println(titles.get(i).text());
                    String anime = titles.get(i).text();
                    MangaGenerale mangaAdd = new MangaGenerale();

                    MangaGenerale mangaFind = mangaGeneraleService.findByTitle(anime);
                    if (mangaFind != null) {
                        mangaAdd = mangaFind;
                    }

                    mangaAdd.setTitle(anime);
                    mangaAdd.setUrlImage(images.get(i));
                    mangaGeneraleService.save(mangaAdd);
                }
                Elements divPages = doc.select("div[class=spaceit]");
                Elements spanPages = divPages.select("span");
                Elements pages = spanPages.select("a");
                int max = 0;
                if (pages.size() > 0) {
                    max = Integer.parseInt(pages.get(pages.size() - 1).text());
                }
                if (max == 20) {
                    int nb = max * 50 - 50;
                    doc = Jsoup.connect(href + "&show=" + nb).get();
                    Elements divPages2 = doc.select("div[class=spaceit]");
                    Elements spanPages2 = divPages2.select("span");
                    Elements pages2 = spanPages2.select("a");

                    if (pages2.size() > 0) {
                        int here = max;

                        max = Integer.parseInt(pages2.get(pages2.size() - 1).text());
                        if (max < here) {
                            max = here;
                        }
                    }
                }
                //  System.out.println(max);
                for (int i = 1; i < max; i++) {
                    String urlBasePage = "https://myanimelist.net" + pages.get(0).attr("href");
                    urlBasePage = urlBasePage.replace("50", "");
                    int m = 50;
                    String p = String.valueOf(Math.multiplyExact(i, m));
                    urlBasePage = urlBasePage + p;
                    doc = Jsoup.connect(urlBasePage).get();
                    //System.out.println(page.attr("href"));
                    imgs = doc.select("img[class=lazyload]");
                    images.clear();
                    for (Element img : imgs) {
                        String str = img.attr("data-src");
                        str = str.replace("r/50x70/", "");

                        images.add(str);
                    }
                    Elements titlespage = doc.select("a[class=hoverinfo_trigger fw-b fl-l]");
                    for (int k = 0; k < titlespage.size(); k++) {
                        String anime = titlespage.get(k).text();
                        MangaGenerale mangaAdd = new MangaGenerale();

                        MangaGenerale mangaFind = mangaGeneraleService.findByTitle(anime);
                        if (mangaFind != null) {
                            mangaAdd = mangaFind;
                        }
                        mangaAdd.setTitle(anime);
                        mangaAdd.setUrlImage(images.get(k));
                        mangaGeneraleService.save(mangaAdd);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addVideo(Episode episode) throws IOException {

    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }
}
