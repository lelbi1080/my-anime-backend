package be.ben.controller;


import be.ben.repository.Anime;
import be.ben.repository.Manga;
import be.ben.repository.MangaGenerale;
import be.ben.service.dao.AnimeService;
import be.ben.service.dao.MangaGeneraleService;
import be.ben.service.dao.MangaService;
import be.ben.site.*;
import be.ben.util.DataBase;
import be.ben.util.Genre;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.QueryException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
public class MangaController {

    @Autowired
    private MangaService mangaService;


    @Autowired
    private AnimeService animeService;
    @Autowired
    private MangaXd mangaXd;

    @Autowired
    private TeleManga teleManga;

    @Autowired
    private MyAnimelList myAnimelList;
    @Autowired
    private MangaGeneraleService mangaGeneraleService;


    @Autowired
    private DataBase dataBase;

    @Autowired
    private OtakuFr otakuFr;

    @Autowired
    private UniversAnime universAnime;

    @Autowired
    private FullAnime fullAnime;

    public static int hammingDistance(String s1, String s2) {
        //   System.out.println("Je compare " + s1 + " avec " + s2);
        int cpt = Math.abs(s1.length() - s2.length());
        int shortest = s1.length() < s2.length() ? s1.length() : s2.length();

        for (int i = 0; i < shortest; i++) {
            if (s1.charAt(i) != s2.charAt(i))
                cpt++;
        }

        return cpt;
    }

    public static String sanitize(String s) {
        return StringUtils.stripAccents(s.toLowerCase()
                .replaceAll("(vostfr|vf|vo|Vostfr|VostFR|VOSTFR)", "")
                .replaceAll("[-=_/ ,()\\\\]", "")
                .replaceAll("tv", "")
                .replaceAll("!","")
                .replaceAll("Integrale", "").replaceAll("Integrale", "").replaceAll(":", ""));
    }

    public static String sanitizeAnimeList(String s) {
        return StringUtils.stripAccents(s.toLowerCase()
                .replaceAll("(vostfr|vf|vo|Vostfr|VostFR|VOSTFR)", "")
                .replaceAll("!","")
                .replaceAll("[-=_/ ,()\\\\]", "").replaceAll(":", ""));
    }

    @CrossOrigin
    @RequestMapping("/mangas")
    public List<Manga> getManga(@RequestParam("start") int start, @RequestParam("end") int end) {
        return mangaService.getBetweenStartToEnd(start, end);
    }

    @CrossOrigin
    @RequestMapping("/addMangas")
    public String addMangas() {
        try {
            mangaXd.addManga();
            teleManga.addManga();
            otakuFr.addManga();
            universAnime.addManga();

            return "Ok";
        } catch (QueryException e) {
            return "Error " + e.getQueryString();
        }
    }

    @CrossOrigin
    @RequestMapping("/addAnimes")
    public String addAnimes() {
        List<Manga> db = mangaService.findAll();
        List<MangaGenerale> myGeneraleList = mangaGeneraleService.findAll();


        //= new String[]{"Dragon Ball Z", "Dragon BaLl-- kaI", "Dragon Ronpa", "Naruto", "Ajin (TV)", "Ajin (TV) 2nd Season", "Bleach", "Ajin vostfr"};
        ArrayList<Map.Entry<Manga, Integer>> distancesPair = new ArrayList<>();
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Manga> mangasToLink = new ArrayList<>();
        for (int j = 0; j < myGeneraleList.size(); j++) {
            distancesPair = new ArrayList<>();
            distances = new ArrayList<>();
            for (int i = 0; i < db.size(); i++) {
                int d = hammingDistance(sanitizeAnimeList(myGeneraleList.get(j).getTitle()), sanitize(db.get(i).getTitle()));
                Map.Entry<Manga, Integer> pair;
                pair = new AbstractMap.SimpleEntry(db.get(i), new Integer(d));
                distances.add(d);
                distancesPair.add(pair);
                // System.out.println("La distance de Hamming entre " + name + " et " + db.get(i).getTitle() + " (sanitized = " + sanitize(db.get(i).getTitle() + ") " + " est de : " + d));
            }
            Integer min = Collections.min(distances);

            mangasToLink = new ArrayList<>();
            for (int k = 0; k < distancesPair.size(); k++) {
                Anime anime = new Anime();
                List<Manga> mangaList = new ArrayList<>();
                if (min <= myGeneraleList.get(j).getTitle().length() / 50 && distancesPair.get(k).getValue() <= myGeneraleList.get(j).getTitle().length() / 50) {
                    if (min <= distancesPair.get(k).getValue()) {
                        mangasToLink.add(distancesPair.get(k).getKey());

                    }
                }
            }
            if (mangasToLink.size() > 0) {
                Anime anime = new Anime();
                anime.setMangaGenerale(myGeneraleList.get(j));

                for (int i = 0; i < mangasToLink.size(); i++) {
                    mangasToLink.get(i).setManga(anime);
                    // mangaService.save(mangasToLink.get(i));
                }
                anime.setMangaList(mangasToLink);
                MangaGenerale mangaGenerale = anime.getMangaGenerale();
                mangaGenerale.setAnime(anime);

                //mangaGeneraleService.save(mangaGenerale);

                animeService.save(anime);

            }


        }

        return mangasToLink.toString();


    }

    @CrossOrigin
    @RequestMapping("/addMangasGenerale")
    public String addMangasGenerale() {
        try {
            myAnimelList.addManga();
            return "Ok";
        } catch (QueryException e) {
            return "Error " + e.getQueryString();
        }
    }


    @CrossOrigin
    @RequestMapping("/test/{name}/{name2}")
    public String testS(@PathVariable String name, @PathVariable String name2) {
        return "name length /4 " + name.length() / 4 + " distance " + hammingDistance(sanitizeAnimeList(name), sanitize(name2)) + " pour " + sanitizeAnimeList(name) + " pour " + sanitize(name2) + " ";
    }

    @CrossOrigin
    @RequestMapping("/animes")
    public List<Anime> animes() {
        return animeService.findAll();
    }

    @CrossOrigin
    @RequestMapping("/addGenre")
    public void addGenre() {
        List<Anime> animes = animeService.findAll();
        for (Anime anime : animes) {
            MangaGenerale mangaGenerale = anime.getMangaGenerale();
            List<String> genres = new ArrayList<>();
            if (mangaGenerale.getUrlImage() != null) {
                try {
                    Thread.sleep(500);
                    Document page = Jsoup.connect(mangaGenerale.getUrlPage()).timeout(60000).get();
                    Elements divs = page.select("div:contains(Genres:)").get(5).select("a");

                    for (Element e : divs) {
                        genres.add(e.text());
                    }
                    mangaGenerale.setGenres(genres);
                    mangaGeneraleService.save(mangaGenerale);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @CrossOrigin
    @PostMapping("/animes/genres")
    public Set<MangaGenerale> searchByGenre(@RequestBody Genre genre) {
        Set<Anime> animes = new HashSet<>();
        Set<MangaGenerale> mangas = new HashSet<>();
        List<Anime> animesList = animeService.findAll();
        for (String s : genre.getGenres()) {
            for (Anime a : animesList) {
                for (String g : a.getMangaGenerale().getGenres()) {
                    if (g.equals(s)) {
                        mangas.add(a.getMangaGenerale());
                        break;
                    }
                }
            }
        }
        return mangas;
    }

    @CrossOrigin
    @RequestMapping("/test2")
    public Set<MangaGenerale> tets2() {
        List<String> genres = new ArrayList<>();
        genres.add("Seinen");
        // genres.add("Drama");
        Genre genre = new Genre(genres);
        return searchByGenre(genre);

    }

    @CrossOrigin
    @RequestMapping("/search/{value}")
    public List<MangaGenerale> searchValue(@PathVariable String value) {
        return this.mangaGeneraleService.findByTitleContainingAndAnimeNotNull(value);

    }
    @CrossOrigin
    @RequestMapping("/animes/title")
    public List<String> animesTitle() {
        return mangaGeneraleService.getAllTitleMapped();
    }

    @CrossOrigin
    @RequestMapping("/animesMapped/{first}/{start}/{end}")
    public List<MangaGenerale> animesMapped(@PathVariable String first, @PathVariable int start, @PathVariable int end) {
        List<MangaGenerale> mangas = mangaGeneraleService.findByAnimeNotNull(first);
        if (end > mangas.size() || start > mangas.size()) {
            return mangas.subList(mangas.size() - 17, mangas.size() - 1);
        } else {
            return mangas.subList(start, end);
        }

    }

    @CrossOrigin
    @RequestMapping("/animesMapped/{first}")
    public List<MangaGenerale> animesMappedLetters(@PathVariable String first) {
        List<MangaGenerale> m= mangaGeneraleService.findByAnimeNotNull(first);

        for (int i=0;i<m.size();i++) {
            Optional<MangaGenerale> mm= mangaGeneraleService.findById(m.get(i).getId());
              if(mm.isPresent()){
                m.get(i).setAnimeListList(mm.get().getAnimeListList());
            }
        }
        return m;
    }

    @CrossOrigin
    @RequestMapping("/start")
    public String start() {
        try {
            /*System.out.println("Start sql ....");
            this.startSql();
            System.out.println("end ");
            System.out.println("start add manga generale");
            this.addMangasGenerale();
            System.out.println("end");
            System.out.println("add mangas");
            this.addMangas();
            System.out.println("end");
            System.out.println("atfer");
            this.after();
            System.out.println("end");
            System.out.println("add animes");

            this.addAnimes();
            System.out.println("end");*/
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "Start completed successfull ";

    }

    @CrossOrigin
    @RequestMapping("/startSql")
    public String startSql() throws SQLException {
        this.dataBase.resetDatabase("start.sql");
        return "start completed  ";

    }


    @CrossOrigin
    @RequestMapping("/addOtakuFr")
    public String addOatkuFr() throws SQLException {
        this.otakuFr.addManga();
        return "start completed  ";

    }


    @CrossOrigin
    @RequestMapping("/addOtakuFrEp")
    public String addOatkuFrEp() throws SQLException {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect("http://www.otakufr.com/anime-list-all/").timeout(60000)
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
                if (mangaFind != null) {
                    mangaAdd = mangaFind;

                    if(mangaAdd.getAnimes()!=null &&mangaAdd.getAnimes().size()>0) {
                        try {
                            doc = Jsoup.connect(href).timeout(60000)
                                    .userAgent("Mozilla")
                                    .get();
                            Elements episodes = doc.select("ul[class=lst]").select("li");
                            otakuFr.episodeAdd(episodes, title, mangaAdd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
  		 }catch (Exception ex){
                ex.printStackTrace();
            }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "start completed  ";

    }
    @CrossOrigin
    @RequestMapping("/addMangaXd")
    public String addMangaXd() throws SQLException {
        this.mangaXd.addManga();
        return "start completed  ";

    }

    @CrossOrigin
    @RequestMapping("/addUniversAnime")
    public String addUniversAnime() throws SQLException {
        this.universAnime.addManga();
        return "start completed  ";

    }
    @CrossOrigin
    @RequestMapping("/addUniversAnimeEp")
    public String addUniversAnimeEp() throws SQLException {
        Document doc = null;
        String page = "";
        Document pageDoc = null;
        try {


            doc = Jsoup.connect("https://www.universanimez.com/liste-des-animes").timeout(60000)
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
                        if(mangaAdd.getAnimes()!=null &&mangaAdd.getAnimes().size()>0) {
                            try {
                                doc = Jsoup.connect(href).userAgent("Mozilla").timeout(60000).get();
                                Elements episodes = doc.select("div.entry-content").select("a");
                                universAnime.episodeAdd(episodes, title, mangaAdd);
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


        return "start completed  ";

    }

    @CrossOrigin
    @RequestMapping("/addTeleManga")
    public String addTeleManga() throws SQLException {
        this.teleManga.addManga();
        return "start completed  ";

    }

    @CrossOrigin
    @RequestMapping("/addTeleMangaEp")
    public String addTeleMangaEp() throws SQLException {
        Document doc;
        String page = "";
        Document pageDoc = null;
        try {

            String a = "09";
            doc = Jsoup.connect("http://www.telemanga.net/classement.php?lettre=" + a).timeout(60000)
                    .userAgent("Mozilla")
                    .get();

            Elements divs = doc.select("div[class=entry-content clearfix]");
            Element title;
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
                        if(manga.getAnimes()!=null &&manga.getAnimes().size()>0) {
                            try {
                                doc = Jsoup.connect(urlMangaPage).timeout(60000)
                                        .userAgent("Mozilla")
                                        .get();
                                Elements episodes = doc.select("div[class=entry-content]").select("a");

                                teleManga.episodeAdd(episodes, titleManga, manga);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                }
            }

            char alphabet = 'A';

            do {
                doc = Jsoup.connect("http://www.telemanga.net/classement.php?lettre=" + alphabet).timeout(60000)
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
                            if(manga.getAnimes()!=null &&manga.getAnimes().size()>0) {
                                try {
                                    doc = Jsoup.connect(urlMangaPage).timeout(60000)
                                            .userAgent("Mozilla")
                                            .get();
                                    Elements episodes = doc.select("div[class=entry-content]").select("a");
                                    teleManga.episodeAdd(episodes, titleManga, manga);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                    }
                }


                alphabet++;
            } while (alphabet <= 'Z');


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "start completed  ";

    }


    @CrossOrigin
    @RequestMapping("/mangas/{title}")
    public MangaGenerale findByTitle(@PathVariable String title) throws SQLException {
        return mangaGeneraleService.findByTitleAndAnimeNotNull(title);

    }


    @CrossOrigin
    @RequestMapping("/afterSql")
    public String after() throws SQLException {
        this.dataBase.resetDatabase("after.sql");
        return "reset completed  ";

    }

    @CrossOrigin
    @RequestMapping("/afterafterSql")
    public String afterafter() throws SQLException {
        this.dataBase.resetDatabase("afterafter.sql");
        return "reset completed  ";

    }

    @CrossOrigin
    @RequestMapping("/mangaGenerales/{id}")
    public MangaGenerale getAnimeMangaGenerale(@PathVariable int id) throws SQLException {
        return this.mangaGeneraleService.findMangaGeneraleById(id);

    }

    @CrossOrigin
    @RequestMapping("/addMangaXdEp")
    public String addMangaXdEp() throws SQLException {

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
        mangaXd.addMangaXdEp();
        return "start completed  ";
    }

    @CrossOrigin
    @RequestMapping("/addOtakufrComp")
    public String addOatakuFrComp() throws SQLException {
        otakuFr.addMangaComp();
        return "start completed  ";
    }

    @CrossOrigin
    @RequestMapping("/addUniversAnimeComp")
    public String addUniversAnimeComp() throws SQLException {
        universAnime.addMangaComp();
        return "start completed  ";
    }


    @CrossOrigin
    @RequestMapping("/addAnimesNew")
    public String addAnimesNew() {
        List<Manga> db = mangaService.findAllNotMapped();
        List<MangaGenerale> myGeneraleList = mangaGeneraleService.findAll();


        //= new String[]{"Dragon Ball Z", "Dragon BaLl-- kaI", "Dragon Ronpa", "Naruto", "Ajin (TV)", "Ajin (TV) 2nd Season", "Bleach", "Ajin vostfr"};
        ArrayList<Map.Entry<Manga, Integer>> distancesPair = new ArrayList<>();
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Manga> mangasToLink = new ArrayList<>();
        for (int j = 0; j < myGeneraleList.size(); j++) {
            distancesPair = new ArrayList<>();
            distances = new ArrayList<>();
            for (int i = 0; i < db.size(); i++) {
                int d = hammingDistance(sanitizeAnimeList(myGeneraleList.get(j).getTitle()), sanitize(db.get(i).getTitle()));
                Map.Entry<Manga, Integer> pair;
                pair = new AbstractMap.SimpleEntry(db.get(i), new Integer(d));
                distances.add(d);
                distancesPair.add(pair);
                // System.out.println("La distance de Hamming entre " + name + " et " + db.get(i).getTitle() + " (sanitized = " + sanitize(db.get(i).getTitle() + ") " + " est de : " + d));
            }
            Integer min = Collections.min(distances);

            mangasToLink = new ArrayList<>();
            for (int k = 0; k < distancesPair.size(); k++) {
                Anime anime = new Anime();
                List<Manga> mangaList = new ArrayList<>();
                if (min <= myGeneraleList.get(j).getTitle().length() / 50 && distancesPair.get(k).getValue() <= myGeneraleList.get(j).getTitle().length() / 50) {
                    if (min <= distancesPair.get(k).getValue()) {
                        mangasToLink.add(distancesPair.get(k).getKey());

                    }
                }
            }
            if (mangasToLink.size() > 0) {
                Anime anime;
                Anime mangaFind = animeService.findAnimeByMangaGenerale(myGeneraleList.get(j));
                if(mangaFind==null){
                    anime=new Anime();
                    anime.setMangaGenerale(myGeneraleList.get(j));
                    for (int i = 0; i < mangasToLink.size(); i++) {
                        mangasToLink.get(i).setManga(anime);
                        // mangaService.save(mangasToLink.get(i));
                    }
                    anime.setMangaList(mangasToLink);
                    MangaGenerale mangaGenerale = anime.getMangaGenerale();
                    mangaGenerale.setAnime(anime);

                    //mangaGeneraleService.save(mangaGenerale);

                    animeService.save(anime);
                }else {
                    anime=mangaFind;
                    for (int i = 0; i < mangasToLink.size(); i++) {
                        mangasToLink.get(i).setManga(anime);
                        // mangaService.save(mangasToLink.get(i));
                    }
                    anime.getMangaList().addAll(mangasToLink);

                    animeService.save(anime);

                }



            }


        }

        return "";


    }


    @CrossOrigin
    @RequestMapping("/addMangasGeneraleNot")
    public String addMangasGeneraleIfNotExist() {
        try {
            myAnimelList.addMangaIfNotExist();
            return "Ok";
        } catch (QueryException e) {
            return "Error " + e.getQueryString();
        }
    }

    @CrossOrigin
    @RequestMapping("/addFullAnimeComp")
    public String addFullAnimeComp() throws SQLException {
        fullAnime.addManga();
        return "start completed  ";
    }

    @CrossOrigin
    @RequestMapping("/fullAnimeSql")
    public String fullAnimeSql() throws SQLException {
        this.dataBase.resetDatabase("fullAnime.sql");
        return "reset completed  ";

    }


}
