package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.metamodel.Feed_;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@SuppressWarnings({ "unchecked", "rawtypes" })
@Interceptors({ ChronoInterceptor.class })
public class FeedService {

	private static final int MAX_CATEGORIES_TO_RETRIEVE = 5;
	private static final int MAX_FEEDS_TO_RETRIEVE = 25;

	@PersistenceContext
	EntityManager em;

	private CriteriaBuilder cb;

	@EJB
	TendencyService tendencyService;

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

	public List<Feed> findAvailableFeedsByCustomFeed(CustomFeed customFeed) {
		List<Feed> feeds = customFeed.getFeeds();
		List<Feed> availableFeeds = findAllFeeds();
		availableFeeds.removeAll(feeds);
		return availableFeeds;
	}

	public List<Feed> findAvailableFeedsByCustomFeedWithFilter(CustomFeed customFeed, String filter) {
		if (filter == null) {
			filter = "";
		}
		filter = filter.toLowerCase();
		List<Feed> feeds = new ArrayList<Feed>();
		if (customFeed != null) {
			feeds.addAll(customFeed.getFeeds());
		}
		Set<Feed> availableFeeds = new HashSet<Feed>();
		availableFeeds.addAll(findFeedsByCategory(filter));
		availableFeeds.removeAll(feeds);

		if (availableFeeds.size() <= MAX_FEEDS_TO_RETRIEVE) {
			availableFeeds.addAll(findFeedsByDescriptionUrlAuthorTitle(filter));
			availableFeeds.removeAll(feeds);
		}

		if (availableFeeds.size() <= MAX_FEEDS_TO_RETRIEVE) {
			availableFeeds.addAll(findFeedsByItemTitle(filter));
			availableFeeds.removeAll(feeds);
		}
		return new ArrayList<Feed>(availableFeeds);
	}

	public List<Feed> findFeedsByItemTitle(String filter) {
		String strQuery = "select distinct f.ID from ITEM i, FEED f where f.id = i.FEED_ID and lower(i.TITLE) like '%"
				+ filter.toLowerCase() + "%'";
		Query query = em.createNativeQuery(strQuery);
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public List<Feed> findFeedsByCategory(String filter) {
		String strQuery = "select distinct f.ID from ITEM i, FEED f where f.id = i.FEED_ID and i.id in (select distinct item_id from ITEM_CATEGORY where lower(CATEGORIES_NAME) like '%"
				+ filter.toLowerCase() + "%')";
		Query query = em.createNativeQuery(strQuery);
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public List<Feed> findFeedsByDescriptionUrlAuthorTitle(String filter) {
		filter = filter.toLowerCase();
		String strQuery = "select ID from FEED where lower(description) like '%" + filter
				+ "%' or lower(title) like '%" + filter + "%' or lower(url) like '%" + filter
				+ "%' or lower(author) like '%" + filter + "%'";
		Query query = em.createNativeQuery(strQuery);
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public Long getNumFeeds() {
		Query query = em.createQuery("select count(f) from Feed f");
		return (Long) query.getSingleResult();
	}

	public List<String> findCategoriesByFeed(Feed feed) {
		String strQuery = "select item_category.CATEGORIES_NAME"
				+ " from FEED feed, ITEM item, ITEM_CATEGORY item_category" + " where feed.ID = " + feed.getId()
				+ " and item.FEED_ID = feed.ID and item_category.ITEM_ID = item.ID"
				+ " group by item_category.CATEGORIES_NAME" + " order by count(item.ID) desc";
		Query query = em.createNativeQuery(strQuery);
		query.setMaxResults(MAX_CATEGORIES_TO_RETRIEVE);
		return query.getResultList();
	}

	public Feed findFeedById(Long id) {
		return em.find(Feed.class, id);
	}

	public void delete(Feed feed) {
		Feed feedToRemove = em.find(Feed.class, feed.getId());
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
		double publicationRatio = tendencyService.computeDayTendency(feed);
		feed.setPublicationRatio(publicationRatio);
		feed = em.merge(feed);
		em.flush();
		return feed;
	}

	public Feed findLastUpdatedFeed() {
		Query query = em.createQuery("select f from Feed f order by f.lastUpdate DESC");
		return getSingleResult(query);
	}

	public Map<String, Long> getNumFeedType() {
		Map<String, Long> maps = new HashMap<String, Long>();
		Query query = em
				.createNativeQuery("select FEEDTYPE, count(*) from FEED group by FEEDTYPE order by FEEDTYPE ASC");
		List list = query.getResultList();
		for (int i = 0; i < list.size(); i++) {
			Object[] value = (Object[]) list.get(i);
			String feedType = (String) value[0];
			Long count;
			if (value[1] instanceof Long) {
				count = (Long) value[1];
			} else {
				count = ((Integer) value[1]).longValue();
			}
			maps.put(feedType, count);
		}
		return maps;
	}

	public void persist(Feed feed) {
		em.persist(feed);
		em.flush();
	}
}
