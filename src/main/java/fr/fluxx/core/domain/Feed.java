/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.fluxx.core.domain;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;

@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "findAllUnusedFeeds", query = "SELECT f.id FROM Feed f WHERE f.id NOT IN (SELECT DISTINCT feed_id FROM CUSTOMFEED_FEED)"),
    @NamedNativeQuery(name = "findAllTopPriorityFeeds", query = "SELECT f.* FROM FEED f WHERE f.id IN (SELECT DISTINCT feed_id FROM CUSTOMFEED_FEED)"),
    @NamedNativeQuery(name = "findFeedsByItemTitle", query = "SELECT DISTINCT f.id FROM ITEM i, FEED f WHERE f.id = i.feed_id AND LOWER(i.title) like ?1"),
    @NamedNativeQuery(name = "findFeedsByCategory", query = "SELECT DISTINCT f.id FROM ITEM i, FEED f WHERE f.id = i.feed_id AND i.id IN (SELECT DISTINCT item_id FROM ITEM_CATEGORY, CATEGORY WHERE LOWER(CATEGORY.name) LIKE ?1 AND CATEGORY.id = ITEM_CATEGORY.categories_id)"),
    @NamedNativeQuery(name = "findFeedsByDescriptionUrlAuthorTitle", query = "SELECT id FROM FEED WHERE LOWER(description) LIKE ?1 OR LOWER(title) LIKE ?2 OR LOWER(url) LIKE ?3 OR LOWER(author) LIKE ?4"),
    @NamedNativeQuery(name = "getNumFeedType", query = "SELECT feedtype, COUNT(*) FROM FEED GROUP BY feedtype ORDER BY feedtype ASC")
})
@NamedQueries({
    @NamedQuery(name = "findLastUpdatedFeed", query = "SELECT f FROM Feed f WHERE f.lastUpdate = (SELECT MAX(g.lastUpdate) FROM Feed g)"),
    @NamedQuery(name = "findFeedsByInError", query = "SELECT f FROM Feed f WHERE f.inError = :inError ORDER BY f.title asc"),
    @NamedQuery(name = "getNumFeeds", query = "SELECT COUNT(f) FROM Feed f"),
    @NamedQuery(name = "findFeedByUrl", query = "SELECT f FROM Feed f WHERE f.url = :url"),
    @NamedQuery(name = "deleteAllFeeds", query = "DELETE FROM Feed f")})
@Table(name = "FEED", uniqueConstraints =
@UniqueConstraint(columnNames = {"URL"}))
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Double MAXIMUM_TIME_OUT_UPDATE = 12D; // max 12 hours before update

    private static final Double MINIMUM_TIME_OUT_UPDATE = 1D; // min 1 hour before update

    private static final Double MAXIMUM_PUBLICATION_RATIO = 100D; // max 100 items per days

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String url;

    @OrderBy("publishedDate DESC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feed")
    private List<Item> items = new ArrayList<Item>();

    @ManyToMany(mappedBy = "feeds", cascade = {DETACH, REFRESH})
    private List<CustomFeed> customFeeds;

    private String author;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

    private String feedType;

    private Boolean inError = false;

    @Column(nullable = false)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastUpdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date nextUpdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date publishedDate;

    @Transient
    private int size;

    private Double publicationRatio;

    public Feed() {
    }

    public Feed(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "title:" + title + " url:" + url;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Feed) {
            Feed f = (Feed) o;
            if (url == null || f.url == null) {
                return false;
            }
            return url.equals(f.url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @PrePersist
    @PreUpdate
    public void calculateNextUpdate() {
        if (getPublicationRatio() == null || getPublicationRatio() == 0) {
            setPublicationRatio(MAXIMUM_PUBLICATION_RATIO);
        }
        int timeOutInHours = (int) (MAXIMUM_TIME_OUT_UPDATE / getPublicationRatio());
        if (timeOutInHours > MAXIMUM_TIME_OUT_UPDATE) {
            timeOutInHours = MAXIMUM_TIME_OUT_UPDATE.intValue();
        } else if (timeOutInHours < MINIMUM_TIME_OUT_UPDATE) {
            timeOutInHours = MINIMUM_TIME_OUT_UPDATE.intValue();
        }
        DateTime date = new DateTime();
        date = date.plusHours(timeOutInHours);
        setNextUpdate(date.toDate());
        setLastUpdate(new Date());
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<CustomFeed> getCustomFeeds() {
        return customFeeds;
    }

    public void setCustomFeeds(List<CustomFeed> customFeeds) {
        this.customFeeds = customFeeds;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Double getPublicationRatio() {
        return publicationRatio;
    }

    public void setPublicationRatio(Double publicationRatio) {
        this.publicationRatio = publicationRatio;
    }

    public Date getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
}
