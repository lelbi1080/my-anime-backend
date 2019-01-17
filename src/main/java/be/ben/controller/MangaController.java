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
                .replaceAll("Integrale", "").replaceAll("Integrale", "").replaceAll(":", ""));
    }

    public static String sanitizeAnimeList(String s) {
        return StringUtils.stripAccents(s.toLowerCase()
                .replaceAll("(vostfr|vf|vo|Vostfr|VostFR|VOSTFR)", "")
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

                if (myGeneraleList.get(j).getTitle().equals("GTO") && db.get(i).getTitle().equals("GTO VostFR")) {
                    int a = myGeneraleList.get(j).getTitle().length() / 4;
                    System.out.println(sanitizeAnimeList(myGeneraleList.get(j).getTitle()) + " et " + sanitize(db.get(i).getTitle()) + " d = " + d + " et size " + a);
                }
                distances.add(d);
                distancesPair.add(pair);
                // System.out.println("La distance de Hamming entre " + name + " et " + db.get(i).getTitle() + " (sanitized = " + sanitize(db.get(i).getTitle() + ") " + " est de : " + d));
            }
            Integer min = Collections.min(distances);

            mangasToLink = new ArrayList<>();
            MangaGenerale m = new MangaGenerale();
            for (int k = 0; k < distancesPair.size(); k++) {
                Anime anime = new Anime();
                List<Manga> mangaList = new ArrayList<>();
                if (myGeneraleList.get(j).getTitle().equals("GTO") && distancesPair.get(k).getKey().getTitle().equals("GTO VostFR")) {
                    System.out.println(min);
                    System.out.println(myGeneraleList.get(j).getTitle().length() / 4);
                    System.out.println(distancesPair.get(k).getValue());
                }
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

    @CrossOrigin(origins = "http://localhost:4200")
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
        return mangaGeneraleService.findByAnimeNotNull(first);
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
    @RequestMapping("/addTeleManga")
    public String addTeleManga() throws SQLException {
        this.teleManga.addManga();
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
    @RequestMapping("/mangaGenerales/{id}")
    public MangaGenerale getAnimeMangaGenerale(@PathVariable int id) throws SQLException {
        return this.mangaGeneraleService.findMangaGeneraleById(id);

    }



}
