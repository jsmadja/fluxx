package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.metamodel.Feed_;

@Stateless
public class FeedService {

    private static final int MAX_CATEGORIES_TO_RETRIEVE = 5;
    private static final int MAX_FEEDS_TO_RETRIEVE = 25;

    @PersistenceContext
    EntityManager em;

    private CriteriaBuilder cb;

    private List<Feed> fromFeedIdListToFeedList(List<Long> feedIds) {
        List<Feed> feeds = new ArrayList<Feed>();
        for (Long id : feedIds) {
            feeds.add(findFeedById(id));
        }
        return feeds;
    }

    private Feed getSingleResult(Query query) {
        List<Feed> feeds = query.getResultList();
        if (feeds.isEmpty()) {
            return null;
        } else {
            return feeds.get(0);
        }
    }

    @PostConstruct
    public void init() {
        cb = em.getCriteriaBuilder();
    }

    public List<Feed> findFeedsByInError(boolean inError, int firstResult) {
        Query q = em.createQuery("SELECT Feed FROM Feed AS feed WHERE feed.inError = :inError order by feed.title");
        q.setParameter("inError", inError);
        q.setFirstResult(firstResult);
        q.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
        return q.getResultList();
    }

    public List<Feed> findAllFeeds() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Feed> feed = cq.from(Feed.class);
        cq.select(feed);
        cq.orderBy(cb.asc(feed.get(Feed_.title)));
        return em.createQuery(cq).getResultList();
    }

    public List<Feed> findAllFeedsNotInError() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Feed> feed = cq.from(Feed.class);
        cq.select(feed);
        cq.where(cb.isFalse(feed.get(Feed_.inError)));
        cq.orderBy(cb.asc(feed.get(Feed_.title)));
        return em.createQuery(cq).getResultList();
    }

    public List<Feed> findAllFeedsInError() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Feed> feed = cq.from(Feed.class);
        cq.select(feed);
        cq.where(cb.isTrue(feed.get(Feed_.inError)));
        cq.orderBy(cb.asc(feed.get(Feed_.title)));
        return em.createQuery(cq).getResultList();
    }

     public List<Feed> findAvailableFeedsByAggregatedFeed(AggregatedFeed aggregatedFeed) {
        List<Feed> feeds = aggregatedFeed.getFeeds();
        List<Feed> availableFeeds = findAllFeeds();
        availableFeeds.removeAll(feeds);
        return availableFeeds;
    }

    public List<Feed> findAvailableFeedsByAggregatedFeedWithFilter(AggregatedFeed aggregatedFeed, String filter) {
        filter = filter.toLowerCase();
        List<Feed> feeds = aggregatedFeed.getFeeds();
        Set<Feed> availableFeeds = new HashSet<Feed>();
        availableFeeds.addAll(findFeedsByCategoryWithLike(filter));
        availableFeeds.removeAll(feeds);

        if (availableFeeds.size() <= MAX_FEEDS_TO_RETRIEVE) {
            availableFeeds.addAll(findFeedsWithLike(filter));
            availableFeeds.removeAll(feeds);
        }

        if (availableFeeds.size() <= MAX_FEEDS_TO_RETRIEVE) {
            availableFeeds.addAll(findFeedsByItemWithLike(filter));
            availableFeeds.removeAll(feeds);
        }
        return new ArrayList<Feed>(availableFeeds);
    }

    public List<Feed> findFeedsByItemWithLike(String filter) {
        String strQuery = "select distinct f.ID from ITEM i, FEED f where f.id = i.FEED_ID and lower(i.TITLE) like '%" + filter + "%'";
        Query query = em.createNativeQuery(strQuery);
        query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
        List<Long> feedIds = query.getResultList();
        return fromFeedIdListToFeedList(feedIds);
    }

    public List<Feed> findFeedsByCategoryWithLike(String filter) {
        String strQuery = "select distinct f.ID from ITEM i, FEED f where f.id = i.FEED_ID and i.id in (select distinct item_id from ITEM_FEEDCATEGORY where lower(FEEDCATEGORIES_NAME) like '%" + filter + "%')";
        Query query = em.createNativeQuery(strQuery);
        query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
        List<Long> feedIds = query.getResultList();
        return fromFeedIdListToFeedList(feedIds);
    }

    public List<Feed> findFeedsWithLike(String filter) {
        String strQuery = "select ID from FEED where lower(description) like '%" + filter + "%' or lower(title) like '%" + filter + "%' or lower(url) like '%" + filter + "%' or lower(author) like '%" + filter + "%'";
        Query query = em.createNativeQuery(strQuery);
        query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
        List<Long> feedIds = query.getResultList();
        return fromFeedIdListToFeedList(feedIds);
    }

    public Long getNumFeeds() {
        Query query = em.createQuery("select count(f) from Feed f");
        return (Long) query.getSingleResult();
    }


    public List<String> findCategoriesByFeedId(String feedId) {
        String strQuery = "select item_category.FEEDCATEGORIES_NAME"
                + " from FEED feed, ITEM item, ITEM_FEEDCATEGORY item_category"
                + " where feed.ID = " + feedId + " and item.FEED_ID = feed.ID and item_category.ITEM_ID = item.ID"
                + " group by item_category.FEEDCATEGORIES_NAME"
                + " order by count(item.ID) desc";
        Query query = em.createNativeQuery(strQuery);
        query.setMaxResults(MAX_CATEGORIES_TO_RETRIEVE);
        return query.getResultList();
    }

    public Feed findFeedById(Long id) {
        return em.find(Feed.class, id);
    }

    public void delete(Feed feed) {
        Feed feedToRemove = em.find(Feed.class, feed.getId());
        List<AggregatedFeed> aggregatedFeeds = feedToRemove.getAggregatedFeeds();
        for (AggregatedFeed af:aggregatedFeeds) {
            af.getFeeds().remove(feedToRemove);
        }
        em.remove(feedToRemove);
        em.flush();
    }

    public Feed findFeedByUrl(String url) {
        Query query = em.createQuery("select f from Feed f where f.url = :url");
        query.setParameter("url", url);
        return getSingleResult(query);
    }

    public Feed update(Feed feed) {
        feed = em.merge(feed);
        em.flush();
        return feed;
    }

    public Feed findLastUpdatedFeed() {
        Query query = em.createQuery("select f from Feed f order by f.lastUpdate DESC");
        return getSingleResult(query);
    }

    @SuppressWarnings("rawtypes")
	public Map<String, Long> getNumFeedType() {
        Map<String, Long> maps = new HashMap<String, Long>();
        Query query = em.createNativeQuery("select FEEDTYPE, count(*) from FEED group by FEEDTYPE order by FEEDTYPE ASC");
        List list = query.getResultList();
        for (int i = 0; i< list.size(); i++) {
            Object[] value = (Object[]) list.get(i);
            String feedType = (String)value[0];
            Long count;
            if (value[1] instanceof Long) {
            	count = (Long) value[1];
            } else {
            	count = ((Integer)value[1]).longValue();
            }
            maps.put(feedType , count);
        }
        return maps;
    }
}