package fr.kaddath.apps.fluxx.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class CustomFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(name = "CUSTOMFEED_FEED", joinColumns = {@JoinColumn(name = "CUSTOMFEED_ID", referencedColumnName = "ID")}, inverseJoinColumns = {@JoinColumn(name = "FEED_ID", referencedColumnName = "ID")})
    @OrderBy("title ASC")
    private List<Feed> feeds;

    @NotNull
    private String username;

    @NotNull
    private String category;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date referentDay;

    private Integer numLastDay;

    public Integer getNumLastDay() {
        return numLastDay;
    }

    public void setNumLastDay(Integer numLastDay) {
        this.numLastDay = numLastDay;
    }

    public Date getReferentDay() {
        return referentDay;
    }

    public void setReferentDay(Date referentDay) {
        this.referentDay = referentDay;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((CustomFeed) obj).id);
    }

    @Override
    public int hashCode() {
        return category.hashCode() + username.hashCode();
    }

    @Override
    public String toString() {
        return category;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
