package fr.kaddath.apps.fluxx.domain.metamodel;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(Feed.class)
public class Feed_ {

    public static volatile SingularAttribute<Feed, Long> id;
    public static volatile SingularAttribute<Feed, String> url;
    public static volatile SingularAttribute<Feed, Boolean> complete;
    public static volatile ListAttribute<Feed, Item> items;
    public static volatile ListAttribute<Feed, CustomFeed_> customFeeds;
    public static volatile SingularAttribute<Feed, String> author;
    public static volatile SingularAttribute<Feed, String> description;
    public static volatile SingularAttribute<Feed, String> encoding;
    public static volatile SingularAttribute<Feed, String> feedType;
    public static volatile SingularAttribute<Feed, Boolean> inError;
    public static volatile SingularAttribute<Feed, String> title;
    public static volatile SingularAttribute<Feed, Date> lastUpdate;
    public static volatile SingularAttribute<Feed, Date> publishedDate;
    public static volatile SingularAttribute<Feed, Double> publicationRatio;

}
