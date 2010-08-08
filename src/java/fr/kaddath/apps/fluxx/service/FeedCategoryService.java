package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.FeedCategory;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class FeedCategoryService {

    @PersistenceContext
    EntityManager em;

    public List<String[]> findNumItemFeedsByCategory() {
        List<String[]> numItemFeedsByCategory = new ArrayList<String[]>();
        Query q = em.createNativeQuery("SELECT fc.NAME, count(ifc.ITEM_ID) FROM feedcategory fc, item_feedcategory ifc WHERE fc.name = ifc.FEEDCATEGORIES_NAME GROUP BY fc.name");
        for (Object o : q.getResultList()) {
            Object[] couple = (Object[]) o;
            numItemFeedsByCategory.add(new String[]{(String) couple[0], couple[1].toString()});
        }
        return numItemFeedsByCategory;
    }

    public FeedCategory findCategoryByName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name argument is required");
        }
        Query q = em.createQuery("SELECT feedCategory FROM FeedCategory feedCategory WHERE feedCategory.name = :name");
        q.setParameter("name", name);
        q.setMaxResults(1);
        List<FeedCategory> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<String> findCategoryNamesInLowerCaseWithLike(String name, Integer numCategories) {
        Query q = em.createNativeQuery("SELECT distinct(lower(name)) FROM FeedCategory WHERE lower(name) like '%"+name+"%' order by lower(name)");
        q.setMaxResults(numCategories);
        return q.getResultList();
    }

    public List<String> findCategoryNamesWithLike(String name, Integer numCategories) {
        Query q = em.createNativeQuery("SELECT name FROM FeedCategory WHERE lower(name) like '%"+name+"%' order by lower(name)");
        q.setMaxResults(numCategories);
        return q.getResultList();
    }

    public FeedCategory create(String categoryName) {
        FeedCategory category = new FeedCategory();
        category.setName(categoryName);
        em.persist(category);
        em.flush();
        return category;
    }
}
