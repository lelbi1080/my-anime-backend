package be.ben.controller;


import be.ben.repository.*;
import be.ben.service.dao.AnimeListService;
import be.ben.service.dao.MangaGeneraleService;
import be.ben.service.dao.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private static int workload = 12;

    @Autowired
    private UserService userService;

    @Autowired
    private MangaGeneraleService mangaGeneraleService;

    @Autowired
    private AnimeListService animeListService;

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);
        return(hashed_password);
    }

    public static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

    @CrossOrigin
    @PostMapping("/subscribe")
    public boolean subscribe(@RequestBody User user) {
        User userFind = userService.findByuserName(user.getUserName());
        if(userFind!=null){
            return false;
        }else {
            String passwordHash=hashPassword(user.getPasswordHash());
            user.setPasswordHash(passwordHash);
            userService.save(user);
            return true;
        }
    }

    @CrossOrigin
    @PostMapping("/login")
    public boolean login(@RequestBody User user) {
        User userFind = userService.findByuserName(user.getUserName());
        if(userFind==null){
            return false;
        }else {
          return  checkPassword(user.getPasswordHash(),userFind.getPasswordHash());
        }
    }


    @CrossOrigin
    @RequestMapping("/addManga/{userName}/{id}")
    public void addManga(@PathVariable String userName,@PathVariable int id)  {
     User user=  userService.findByuserName(userName);
     MangaGenerale mangaGenerale= mangaGeneraleService.findById(id);
     AnimeList animeList=new AnimeList();
     animeList.setEpisode("1");
     animeList.setManga(mangaGenerale);
     animeList.setUser(user);
     animeListService.save(animeList);
    }

    @CrossOrigin
    @RequestMapping("/getMangas/{userName}")
    public List<MangaGenerale> getMangas(@PathVariable String userName)  {
      User user= userService.findByuserName(userName);
      if(user!=null) {
          List<AnimeList> animeLists = user.getAnimeLists();
          List<MangaGenerale> mangaGenerales = new ArrayList<>();
          animeLists.forEach((a) -> mangaGenerales.add(a.getManga()));
          return mangaGenerales;
      }else{
          return null;
      }


    }



    @CrossOrigin
    @RequestMapping("/updateEpisode/{userName}/{idManga}/{episode}")
    public void updateEpisode(@PathVariable String userName,@PathVariable int idManga,@PathVariable String episode)  {
        System.out.println("userName "+userName);
        System.out.println(idManga+" idManga");
        System.out.println(episode+" episode");
        User u = this.userService.findByuserName(userName);
        this.animeListService.updateEpisode(u.getId(),idManga,episode);
    }






}
