package fr.kaddath.apps.fluxx.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="FLUXXER")
public class Fluxxer implements Serializable {

    @Id
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String email;

    private String twitterAccount;

    private Boolean twitterNotification;

    private Boolean mailNotification;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date signinDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="fluxxer")
    @OrderBy("name")
    private List<AggregatedFeed> aggregatedFeeds;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("name")
    private List<FeedCategory> favoriteCategories;

    public List<FeedCategory> getFavoriteCategories() {
        return favoriteCategories;
    }

    public void setFavoriteCategories(List<FeedCategory> favoriteCategories) {
        this.favoriteCategories = favoriteCategories;
    }

    public List<AggregatedFeed> getAggregatedFeeds() {
        return aggregatedFeeds;
    }

    public void setAggregatedFeeds(List<AggregatedFeed> aggregatedFeeds) {
        this.aggregatedFeeds = aggregatedFeeds;
    }


    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getSigninDate() {
        return signinDate;
    }

    public void setSigninDate(Date signinDate) {
        this.signinDate = signinDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public Boolean getMailNotification() {
        return mailNotification;
    }

    public void setMailNotification(Boolean mailNotification) {
        this.mailNotification = mailNotification;
    }

    public Boolean getTwitterNotification() {
        return twitterNotification;
    }

    public void setTwitterNotification(Boolean twitterNotification) {
        this.twitterNotification = twitterNotification;
    }

}
