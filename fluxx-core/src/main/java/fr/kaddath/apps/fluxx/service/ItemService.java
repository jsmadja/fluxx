package fr.kaddath.apps.fluxx.service;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.time.DateUtils;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.Item_;

@Stateless
public class ItemService {

    public static final int MAX_ITEM_TO_RETRIEVE = 20;

    @PersistenceContext
    EntityManager em;
    private CriteriaBuilder cb;

    private long getNumItemsBetween(Date from, Date to) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Item> item = cq.from(Item.class);
        cq.select(cb.count(item));
        Predicate beginDateClause = cb.greaterThanOrEqualTo(item.get(Item_.publishedDate), from);
        Predicate endDateClause = cb.lessThan(item.get(Item_.publishedDate), to);
        Predicate whereClauses = cb.and(beginDateClause, endDateClause);
        cq.where(whereClauses);
        Query query = em.createQuery(cq);
        Long count = (Long) query.getSingleResult();
        //LOG.log(Level.INFO, "{0} items between {1} and {2}", new Object[]{count, from, to});
        return count;
    }

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
    public void init() {
        cb = em.getCriteriaBuilder();
    }

    public Item findItemByLink(String link) {
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

    public long getNumItemsByDay(Date date) {

        Calendar calendar = getInstance();
        calendar.setTime(date);

        Date from = new GregorianCalendar(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH)).getTime();
        Date to = new Date(from.getTime());
        to = DateUtils.addDays(to, 1);

        return getNumItemsBetween(from, to);
    }

    public Item getFirstItem() {
        Query query = em.createQuery("SELECT i FROM Item i ORDER BY i.publishedDate ASC");
        query.setMaxResults(1);
        return getUniqueResult(query);
    }

    public Item getLastItem() {
        Query query = em.createQuery("SELECT i FROM Item i ORDER BY i.publishedDate DESC");
        query.setMaxResults(1);
        return getUniqueResult(query);
    }

    public long getNumItemsByHour(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);

        Date from = new GregorianCalendar(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), calendar.get(HOUR_OF_DAY), 0, 0).getTime();
        Date to = new Date(from.getTime());
        to = DateUtils.addHours(to, 1);

        return getNumItemsBetween(from, to);
    }
}