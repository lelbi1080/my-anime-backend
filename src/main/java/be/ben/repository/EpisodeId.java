package be.ben.repository;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EpisodeId implements Serializable {
    private String numEp;

    private String titleManga;

    public EpisodeId() {
    }

    public String getNumEp() {
        return numEp;
    }

    public void setNumEp(String numEp) {
        this.numEp = numEp;
    }

    public String getTitleManga() {
        return titleManga;
    }

    public void setTitleManga(String titleManga) {
        this.titleManga = titleManga;
    }
}
