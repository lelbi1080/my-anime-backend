package be.ben.util;

import java.io.Serializable;

public class EpisodeValue implements Serializable {
    private int value;
    private String reference;

    public EpisodeValue(int value, String reference) {
        this.value = value;
        this.reference = reference;
    }

    public EpisodeValue() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
