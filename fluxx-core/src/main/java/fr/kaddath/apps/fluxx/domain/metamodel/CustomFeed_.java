package fr.kaddath.apps.fluxx.domain.metamodel;

import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(CustomFeed.class)
public class CustomFeed_ {

    public static volatile SingularAttribute<CustomFeed, String> id;
    public static volatile SingularAttribute<CustomFeed, String> name;
    public static volatile SingularAttribute<CustomFeed, Integer> numLastDay;
    public static volatile ListAttribute<CustomFeed, Feed> feeds;
    public static volatile SingularAttribute<CustomFeed, Date> referentDay;
    public static volatile SingularAttribute<CustomFeed, String> username;
    public static volatile SingularAttribute<CustomFeed, String> category;

}
