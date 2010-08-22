package fr.kaddath.apps.fluxx.domain.metamodel;

import java.util.Date;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import fr.kaddath.apps.fluxx.domain.Item;

@StaticMetamodel(Item.class)
public class Item_ {
    public static volatile SingularAttribute<Item, Feed> feed;
    public static volatile SingularAttribute<Item, Long> id;
    public static volatile SingularAttribute<Item, Date> publishedDate;
    public static volatile SingularAttribute<Item, Date> updatedDate;
    public static volatile SingularAttribute<Item, String> author;
    public static volatile SingularAttribute<Item, String> title;
    public static volatile SingularAttribute<Item, String> description;
    public static volatile SingularAttribute<Item, String> link;
    public static volatile SingularAttribute<Item, String> descriptionType;
    public static volatile SetAttribute<Item, FeedCategory> feedCategories;
    public static volatile SetAttribute<Item, DownloadableItem> downloadableItems;
    public static volatile SingularAttribute<Item, String> uri;

}
