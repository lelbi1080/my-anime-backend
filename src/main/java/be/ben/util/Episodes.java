package be.ben.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Episodes implements Serializable {
    private List<EpisodeValue> episodes = new ArrayList<>();
    private List<EpisodeValue> s2 = new ArrayList<>();
    private List<EpisodeValue> s3 = new ArrayList<>();
    private List<EpisodeValue> s4 = new ArrayList<>();
    private List<EpisodeValue> oav = new ArrayList<>();
    private List<EpisodeValue> others = new ArrayList<>();
    private List<EpisodeValue> specials = new ArrayList<>();

    public Episodes() {
    }

    public static final Comparator<EpisodeValue> DESCENDING_COMPARATOR = new Comparator<EpisodeValue>() {
        // Overriding the compare method to sort the age
        public int compare(EpisodeValue d, EpisodeValue d1) {
            return d.getValue() - d1.getValue();
        }
    };
    public Episodes(List<String> epiString) {
        EpisodeValue episodeValue;
        for (int i = 0; i < epiString.size(); i++) {
			try{
            String ref = epiString.get(i);
			String refi=ref.replaceAll("[^0-9]", "");
			if(ref!=null&&ref!=""&&refi!=null&&refi!=""){
            episodeValue = new EpisodeValue(Integer.valueOf(ref.replaceAll("[^0-9]", "")), ref);
			if(ref!=null&&ref!=""){
            if (ref.contains("s2")) {
                s2.add(episodeValue);
            } else if (ref.contains("s3")) {
                s3.add(episodeValue);
            } else if (ref.contains("s4")) {
                s4.add(episodeValue);
            } else if (ref.contains("OAV") || ref.contains("oav") || ref.contains("ova") ||
                    ref.contains("OVA") | ref.contains("Oav")) {
                oav.add(episodeValue);
            } else if (ref.matches("[0-9]+")) {
                episodes.add(episodeValue);
            } else if (ref.contains("special") | ref.contains("Special") | ref.contains("SPECIAL")) {
                specials.add(episodeValue);
            } else {
                others.add(episodeValue);
            }
			}
			}
			}catch(Exception ex){
			}
        }


        this.episodes.sort(DESCENDING_COMPARATOR);
    }

    public List<EpisodeValue> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeValue> episodes) {
        this.episodes = episodes;
    }

    public List<EpisodeValue> getS2() {
        return s2;
    }

    public void setS2(List<EpisodeValue> s2) {
        this.s2 = s2;
    }

    public List<EpisodeValue> getS3() {
        return s3;
    }

    public void setS3(List<EpisodeValue> s3) {
        this.s3 = s3;
    }

    public List<EpisodeValue> getS4() {
        return s4;
    }

    public void setS4(List<EpisodeValue> s4) {
        this.s4 = s4;
    }

    public List<EpisodeValue> getOav() {
        return oav;
    }

    public void setOav(List<EpisodeValue> oav) {
        this.oav = oav;
    }

    public List<EpisodeValue> getOthers() {
        return others;
    }

    public void setOthers(List<EpisodeValue> others) {
        this.others = others;
    }
}
