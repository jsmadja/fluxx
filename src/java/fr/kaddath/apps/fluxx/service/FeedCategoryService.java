package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.FeedCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

@Stateless
public class FeedCategoryService {

    @PersistenceContext
    EntityManager em;

    private static final Logger LOG = Logger.getLogger("fluxxer");

    public List<String[]> findNumItemFeedsByCategory() {

        List<String[]> numItemFeedsByCategory = new ArrayList<String[]>();
        Query q = em.createNativeQuery("SELECT fc.name, count( ifc.item ), fc.id FROM feed_category fc, item_feed_categories ifc WHERE fc.id = ifc.feed_categories GROUP BY fc.name");

        for (Object o : q.getResultList()) {

            Object[] couple = (Object[]) o;
            numItemFeedsByCategory.add(new String[]{(String) couple[0], couple[1].toString(), couple[2].toString()});

        }

        return numItemFeedsByCategory;
    }

    public FeedCategory findCategoryByName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name argument is required");
        }
        Query q = em.createQuery("SELECT FeedCategory FROM FeedCategory AS feedCategory WHERE feedCategory.name = :name");
        q.setParameter("name", name);
        q.setMaxResults(1);
        List<FeedCategory> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<String> findCategoryByNameWithLike(String name, Integer numCategories) {
        Query q = em.createNativeQuery("SELECT distinct(lower(name)) FROM FeedCategory WHERE lower(name) like '%"+name+"%' order by lower(name)");
        q.setMaxResults(numCategories);
        return q.getResultList();
    }
}
