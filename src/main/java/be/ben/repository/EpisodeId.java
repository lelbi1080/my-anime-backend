package be.ben.repository;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EpisodeId implements Serializable {
    private String numEp;

    private String titleManga;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
