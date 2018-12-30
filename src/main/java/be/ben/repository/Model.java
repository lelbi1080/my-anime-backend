package be.ben.repository;

import java.io.Serializable;

public class Model implements Serializable {
    private String value;

    public Model(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
