package fr.kaddath.apps.fluxx.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FeedCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String name;

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

}
