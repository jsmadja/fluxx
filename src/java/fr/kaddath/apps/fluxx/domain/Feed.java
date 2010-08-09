package fr.kaddath.apps.fluxx.domain;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OrderBy;
import static javax.persistence.CascadeType.ALL;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    private String url;

    private Boolean complete = false;

    @OrderBy("publishedDate DESC")
    @OneToMany(cascade = {ALL}, mappedBy="feed")
    private List<Item> items = new ArrayList<Item>();

    private String author;

    @Lob
    private String description;

    private String encoding;

    private String feedType;

    private Boolean inError = false;

    @NotNull
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedDate;

    @Override
    public String toString() {
        return "title:"+title+" url:"+url;
    }

    @Override
    public boolean equals(Object o) {
        return url.equals(((Feed)o).url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
    
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public Boolean getInError() {
        return inError;
    }

    public void setInError(Boolean inError) {
        this.inError = inError;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
