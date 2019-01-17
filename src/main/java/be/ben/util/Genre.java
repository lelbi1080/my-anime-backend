package be.ben.util;

import java.util.List;

public class Genre {
    private List<String> genres;

    public Genre(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Genre() {
    }
}
