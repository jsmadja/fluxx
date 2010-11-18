package fr.kaddath.apps.fluxx.domain.metamodel;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import fr.kaddath.apps.fluxx.domain.Fluxxer;

@StaticMetamodel(Fluxxer.class)
public class Fluxxer_ {

    public static volatile SingularAttribute<Fluxxer, String> username;
    public static volatile SingularAttribute<Fluxxer, String> password;
    public static volatile SingularAttribute<Fluxxer, String> email;
    public static volatile SingularAttribute<Fluxxer, String> twitterAccount;
    public static volatile SingularAttribute<Fluxxer, Boolean> twitterNotification;
    public static volatile SingularAttribute<Fluxxer, Boolean> mailNotification;
    public static volatile SingularAttribute<Fluxxer, Date> lastLoginDate;
    public static volatile SingularAttribute<Fluxxer, Date> signinDate;
    public static volatile ListAttribute<Fluxxer, AggregatedFeed> aggregatedFeeds;
    public static volatile ListAttribute<Fluxxer, FeedCategory> favoriteCategories;

}
