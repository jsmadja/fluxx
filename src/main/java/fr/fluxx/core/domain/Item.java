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
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@NamedQueries({
    @NamedQuery(name = "findItemByLink", query = "SELECT i FROM Item i WHERE i.link = :link"),
    @NamedQuery(name = "findItemsByLinkWithFeed", query = "SELECT i FROM Item i WHERE i.feed = :feed AND i.link = :link"),
    @NamedQuery(name = "findLastItem", query = "SELECT i FROM Item i WHERE i.publishedDate = (SELECT MAX(j.publishedDate) FROM Item j)"),
    @NamedQuery(name = "findFirstItem", query = "SELECT i FROM Item i WHERE i.publishedDate = (SELECT MIN(j.publishedDate) FROM Item j)"),
    @NamedQuery(name = "findFirstItemOfFeed", query = "SELECT i FROM Item i WHERE i.feed = :feed ORDER BY i.publishedDate asc"),
    @NamedQuery(name = "findLastItemOfFeed", query = "SELECT i FROM Item i WHERE i.feed = :feed ORDER BY i.publishedDate desc"),
    @NamedQuery(name = "getNumItemsOfFeed", query = "SELECT COUNT(i) FROM Item i WHERE i.feed = :feed"),
    @NamedQuery(name = "getNumItems", query = "SELECT COUNT(i) FROM Item i"),
    @NamedQuery(name = "deleteAllItems", query = "DELETE FROM Item i")})
@Table(name = "ITEM", uniqueConstraints =
@UniqueConstraint(columnNames = {"LINK"}))
public class Item implements Comparable<Item>, Serializable {

    private static final long serialVersionUID = 2707672054863355365L;

    public static final int MAX_ITEM_LINK_SIZE = 512;

    public static final int MAX_ITEM_TITLE_SIZE = 512;

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = MAX_ITEM_LINK_SIZE, nullable = false)
    private String link;

    private String author;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

    private String descriptionType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date publishedDate;

    @Column(length = MAX_ITEM_TITLE_SIZE, nullable = false)
    private String title;

    @ManyToMany(cascade = {DETACH, MERGE, REFRESH})
    private Set<DownloadableItem> downloadableItems = new HashSet<DownloadableItem>();

    @ManyToMany(cascade = {DETACH, MERGE, REFRESH})
    private Set<Category> categories = new HashSet<Category>();

    @ManyToOne
    private Feed feed;

    public Item(String link, String title, Feed feed, Date publishedDate) {
        this.link = link;
        this.title = title;
        this.feed = feed;
        this.publishedDate = publishedDate;
    }

    public Item() {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Item) {
            Item i = (Item) o;
            if (link == null || i.link == null) {
                return false;
            }
            return link.equals(i.link);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }

    @Override
    public String toString() {
        return getTitle();
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

    public String getDescriptionType() {
        return descriptionType;
    }

    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    public Set<DownloadableItem> getDownloadableItems() {
        return downloadableItems;
    }

    public void setDownloadableItems(Set<DownloadableItem> downloadableItems) {
        this.downloadableItems = downloadableItems;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    @Override
    public int compareTo(Item i) {
        return i.getPublishedDate().compareTo(publishedDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
