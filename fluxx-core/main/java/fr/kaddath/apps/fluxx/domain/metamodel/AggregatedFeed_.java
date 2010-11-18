package fr.kaddath.apps.fluxx.domain.metamodel;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;

@StaticMetamodel(AggregatedFeed.class)
public class AggregatedFeed_ {

    public static volatile SingularAttribute<AggregatedFeed, String> id;
    public static volatile SingularAttribute<AggregatedFeed, String> name;
    public static volatile SingularAttribute<AggregatedFeed, String> aggregatedFeedId;
    public static volatile SingularAttribute<AggregatedFeed, Integer> numLastDay;
    public static volatile ListAttribute<AggregatedFeed, Feed> feeds;
    public static volatile SingularAttribute<AggregatedFeed, Date> referentDay;
    public static volatile SingularAttribute<AggregatedFeed, Fluxxer> fluxxer;

}
