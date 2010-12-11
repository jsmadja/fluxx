package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.Item_;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.util.Calendar.*;

@Stateless
@SuppressWarnings("unchecked")
@Interceptors({ChronoInterceptor.class})
public class ItemService {

    public static final int MAX_ITEM_TO_RETRIEVE = Integer.MAX_VALUE;

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
        // LOG.log(Level.INFO, "{0} items between {1} and {2}", new Object[]{count, from, to});
        return (Long) query.getSingleResult();
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
        if (link != null) {
            Query query = em.createNamedQuery("findItemsByLink");
            query.setParameter("link", link);
            return getUniqueResult(query);
        }
        throw new InvalidParameterException("link is a mandatory parameter");
    }

    public List<Item> findItemsOfFeedAndAfterDate(Feed feed, Date date) {
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
        Query query = em.createNamedQuery("getNumItems");
        return (Long) query.getSingleResult();
    }

    public long getNumItemsOfFeed(Feed feed) {
        Query query = em.createNamedQuery("getNumItemsOfFeed");
        query.setParameter("feed", feed);
        return (Long) query.getSingleResult();
    }

    public Item findLastItemOfFeed(Feed feed) {
        Query query = em.createNamedQuery("findLastItemOfFeed");
        query.setParameter("feed", feed);
        return getUniqueResult(query);
    }

    public Item findFirstItemOfFeed(Feed feed) {
        Query query = em.createNamedQuery("findFirstItemOfFeed");
        query.setParameter("feed", feed);
        return getUniqueResult(query);
    }

    public long getNumItemsByDay(Date date) {

        Calendar calendar = getInstance();
        calendar.setTime(date);

        Date from = new GregorianCalendar(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH))
                .getTime();
        Date to = new Date(from.getTime());
        to = DateUtils.addDays(to, 1);

        return getNumItemsBetween(from, to);
    }

    public Item findFirstItem() {
        Query query = em.createNamedQuery("findFirstItem");
        query.setMaxResults(1);
        return getUniqueResult(query);
    }

    public Item findLastItem() {
        Query query = em.createNamedQuery("findLastItem");
        query.setMaxResults(1);
        return getUniqueResult(query);
    }

    public long getNumItemsByHour(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);

        Date from = new GregorianCalendar(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH),
                calendar.get(HOUR_OF_DAY), 0, 0).getTime();
        Date to = new Date(from.getTime());
        to = DateUtils.addHours(to, 1);

        return getNumItemsBetween(from, to);
    }

    public Item findItemByLinkWithFeed(String link, Feed feed) {
        Query query = em.createNamedQuery("findItemsByLinkWithFeed");
        query.setParameter("link", link);
        query.setParameter("feed", feed);
        return getUniqueResult(query);
    }

    public Item store(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            item = em.merge(item);
        }
        em.flush();
        return item;
    }

}
