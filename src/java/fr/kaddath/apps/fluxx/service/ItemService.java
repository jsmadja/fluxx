package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.metamodel.Item_;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class ItemService {

    public static final int MAX_ITEM_TO_RETRIEVE = 20;
    private static final Logger LOG = Logger.getLogger(ItemService.class.getName());

    @PersistenceContext
    EntityManager em;

    private CriteriaBuilder cb;

    private Item getUniqueResult(Query query) {
        query.setMaxResults(1);
        List<Item> items = query.getResultList();
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    @PostConstruct
    private void init() {
        cb = em.getCriteriaBuilder();
    }

    public Item findItemsByLink(String link) {
        Query query = em.createNamedQuery("findItemsByLink");
        query.setParameter("link", link);
        return getUniqueResult(query);
    }

    public List<Item> findItemsByFeedAndAfter(Feed feed, Date date) {
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);

        Root<Item> item = cq.from(Item.class);
        cq.select(item);

        Predicate feedClause = cb.equal(item.get(Item_.feed), feed);
        Predicate dateClause = cb.greaterThanOrEqualTo(item.get(Item_.publishedDate), date);
        Predicate whereClauses = cb.and(feedClause, dateClause);
        cq.where(whereClauses);

        cq.orderBy(cb.desc(item.get(Item_.publishedDate)));
        Query query = em.createQuery(cq);
        query.setMaxResults(MAX_ITEM_TO_RETRIEVE);
        return query.getResultList();
    }

    public List<Item> findItemsByFeed(Feed feed) {
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);

        Root<Item> item = cq.from(Item.class);
        cq.select(item);

        Predicate feedClause = cb.equal(item.get(Item_.feed), feed);
        cq.where(feedClause);

        cq.orderBy(cb.desc(item.get(Item_.publishedDate)));
        Query query = em.createQuery(cq);
        query.setMaxResults(MAX_ITEM_TO_RETRIEVE);
        return query.getResultList();
    }

    public Long getNumItems() {
        Query query = em.createQuery("select count(i) from Item i");
        return (Long) query.getSingleResult();
    }

    public long getNumItems(Feed feed) {
        Query query = em.createQuery("select count(i) from Item i where i.feed = :feed");
        query.setParameter("feed", feed);
        return (Long) query.getSingleResult();
    }

    public Item findLastItem(Feed feed) {
        Query query = em.createQuery("select i from Item i where i.feed = :feed order by i.publishedDate desc");
        query.setParameter("feed", feed);
        return getUniqueResult(query);
    }

    public Item findFirstItem(Feed feed) {
        Query query = em.createQuery("select i from Item i where i.feed = :feed order by i.publishedDate asc");
        query.setParameter("feed", feed);
        return getUniqueResult(query);
    }
}
