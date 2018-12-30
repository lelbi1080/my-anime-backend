package be.ben.site;

import be.ben.repository.Episode;
import be.ben.repository.EpisodeId;
import be.ben.repository.Manga;
import be.ben.service.dao.EpisodeService;
import be.ben.service.dao.MangaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JetAnime extends Site {

    @Autowired
    private MangaService mangaService;

    @Autowired
    private EpisodeService episodeService;
    private String urlMangas = "https://www.jetanime.co";

    @Override
    public void addManga() {
        createManga(urlMangas);
    }

    @Override
    public void createManga(String url) {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements accordeon = doc.select("select[class=form-control]");
            Elements links = accordeon.select("option");
            //         System.out.println(links.get(0).text());
            Element link;
            for (int j = 0; j < links.size(); j++) {
                link = links.get(j);

                Manga manga = new Manga();
                String title = link.text();
                String titleRefactor;


                Manga mangaFind = mangaService.ifExistTitle(title, "JetAnime");
                if (mangaFind != null) {
                    manga = mangaFind;
                }

                manga.setType("JetAnime");
                manga.setTitle(title);
                manga.setTitleOriginal(title);
                mangaService.save(manga);
                doc = Jsoup.connect(url + link.attr("value")).get();
                Elements div = doc.select("div[class=items]");
                Elements episodes = div.select("a[class=list-group-item]");
                Elements currentEpisode = div.select("a[class=list-group-item active]");
                episodeAdd(currentEpisode, episodes, title, manga, link.attr("value"));


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void episodeAdd(Elements currentEpisode, Elements episodes, String title, Manga mangaAdd, String titleRefractor) throws IOException {
        int j = 1;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            EpisodeId episodeId = new EpisodeId();
            episodeId.setNumEp("");
            episodeId.setTitleManga(title);
            Episode episode = new Episode();
            episode.setEpisode_id(episodeId);
            episode.setManga(mangaAdd);
            episode.setUrl(urlMangas + episodes.get(i).attr("href"));
            episodeService.save(episode);
            addVideo(episode);
            j++;
        }
        EpisodeId episodeId = new EpisodeId();
        episodeId.setNumEp("");
        episodeId.setTitleManga(title);
        Episode episode = new Episode();
        episode.setEpisode_id(episodeId);
        episode.setManga(mangaAdd);
        episode.setUrl(urlMangas + currentEpisode.get(0).attr("href"));
        episodeService.save(episode);
        addVideo(episode);
    }

    @Override
    public void addVideo(Episode episode) throws IOException {
       /* try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            //webClient.getOptions().setThrowExceptionOnScriptError(false);

            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setCssEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setTimeout(60000);
            webClient.setJavaScriptTimeout(60000);
            webClient.waitForBackgroundJavaScript(120000);

        HtmlPage a = webClient.getPage("https://www.ianimes.co/index.php");
            webClient.waitForBackgroundJavaScript(100000);
            System.out.println(a.asXml());
        }catch (Exception ex){
            //
        }*/
        /*Document d =Jsoup.connect(episode.getUrl())
                .header("Accept-Encoding", "gzip, deflate")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .maxBodySize(0)
                .timeout(600000)
                .get();
        System.out.println(d.body());*/
        /*WebDriver driver = new PhantomJSDriver(DesiredCapabilities.phantomjs());
        driver.get(episode.getUrl());
        System.out.println(driver);
        driver.quit();*/



       /* try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            HtmlPage page = webClient.getPage(episode.getUrl());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.waitForBackgroundJavaScript(50000);
            System.out.println(page.asXml());

        } catch (IOException ex ) {
            ex.printStackTrace();
        }*/
// Set executable file path to system variable phantomjs.binary.path's value.
        String phantomjsExeutableFilePath = "C:/Users/bilel/Desktop/phantomjs-2.1.1-windows/bin/phantomjs.exe";
        System.setProperty("phantomjs.binary.path", phantomjsExeutableFilePath);


        WebDriver driver = new PhantomJSDriver();


        //Must make the web browser full size other wise it can not parse out result by xpath.
        driver.manage().window().maximize();

        driver.get(episode.getUrl());

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print out yahoo home page title.


        System.out.println(driver.getPageSource());


    }

    public void addVideo(String urlEpisode, Episode episode) throws IOException {

    }

    @Override
    public String getMaxPage(String url) {
        return null;
    }

}
