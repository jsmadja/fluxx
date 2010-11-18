package fr.kaddath.apps.fluxx.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="AGGREGATEDFEED",uniqueConstraints=@UniqueConstraint(columnNames={"AGGREGATEDFEEDID"}))
public class AggregatedFeed implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    private String aggregatedFeedId;

  @ManyToMany
  @JoinTable(
      name="AGGREGATEDFEED_FEED",
      joinColumns={@JoinColumn(name="AGGREGATEDFEED_ID", referencedColumnName="ID")},
      inverseJoinColumns={@JoinColumn(name="FEED_ID", referencedColumnName="ID")})
    @OrderBy("title ASC")
    private List<Feed> feeds;

    @NotNull
    @ManyToOne
    private Fluxxer fluxxer;

    @NotNull
    private String name;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date referentDay;
            
    private Integer numLastDay;

    public String getAggregatedFeedId() {
        return aggregatedFeedId;
    }

    public void setAggregatedFeedId(String aggregatedFeedId) {
        this.aggregatedFeedId = aggregatedFeedId;
    }

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
        return aggregatedFeedId.equals(((AggregatedFeed)obj).aggregatedFeedId);
    }

    @Override
    public int hashCode() {
        return aggregatedFeedId.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public Fluxxer getFluxxer() {
        return fluxxer;
    }

    public void setFluxxer(Fluxxer fluxxer) {
        this.fluxxer = fluxxer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
