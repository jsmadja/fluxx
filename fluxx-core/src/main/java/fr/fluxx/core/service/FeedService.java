/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.fluxx.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.metamodel.Feed_;

@Stateless
@SuppressWarnings({ "unchecked" })
public class FeedService {

	private static final int MAX_CATEGORIES_TO_RETRIEVE = 5;
	private static final int MAX_FEEDS_TO_RETRIEVE = 10;

	@PersistenceContext
	private EntityManager em;

	private CriteriaBuilder cb;

	@EJB
	private TendencyService tendencyService;

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
		Query q = em.createNamedQuery("findFeedsByInError");
		q.setParameter("inError", inError);
		q.setFirstResult(firstResult);
		q.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		return q.getResultList();
	}

	public List<Feed> findFeedsToUpdate() {
		CriteriaQuery<Feed> cq = em.getCriteriaBuilder().createQuery(Feed.class);
		Root<Feed> feed = cq.from(Feed.class);
		cq.select(feed);
		Predicate inErrorClause = cb.isFalse(feed.get(Feed_.inError));
		Predicate nextUpdateClause = cb.lessThan(feed.get(Feed_.nextUpdate), cb.currentTimestamp());
		Predicate whereClauses = cb.and(inErrorClause, nextUpdateClause);
		cq.where(whereClauses);
		cq.orderBy(cb.asc(feed.get(Feed_.title)));
		return em.createQuery(cq).getResultList();
	}

	public List<Feed> findAllTopPriorityFeeds() {
		String strQuery = "SELECT f.* FROM FEED f WHERE f.id IN (SELECT DISTINCT feed_id FROM CUSTOMFEED_FEED)";
		return em.createNativeQuery(strQuery).getResultList();
	}

	public List<Feed> findAllFeedsInError() {
		CriteriaQuery<Feed> cq = em.getCriteriaBuilder().createQuery(Feed.class);
		Root<Feed> feed = cq.from(Feed.class);
		cq.select(feed);
		cq.where(cb.isTrue(feed.get(Feed_.inError)));
		cq.orderBy(cb.asc(feed.get(Feed_.title)));
		return em.createQuery(cq).getResultList();
	}

	public List<Feed> findAvailableFeedsByCustomFeed(CustomFeed customFeed) {
		List<Feed> feeds = customFeed.getFeeds();
		List<Feed> availableFeeds = findFeedsToUpdate();
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
		String strQuery = "select distinct f.ID from ITEM i, FEED f where f.id = i.FEED_ID and i.id in (select distinct item_id from ITEM_CATEGORY, CATEGORY where lower(CATEGORY.NAME) like '%"
				+ filter.toLowerCase() + "%' and CATEGORY.ID = ITEM_CATEGORY.CATEGORIES_ID)";
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
		Query query = em.createNamedQuery("getNumFeeds");
		return (Long) query.getSingleResult();
	}

	public List<String> findCategoriesByFeed(Feed feed) {
		String strQuery = "select category.NAME"
				+ " from FEED feed, ITEM item, ITEM_CATEGORY item_category, CATEGORY category" //
				+ " where feed.ID = " + feed.getId() //
				+ " and item.FEED_ID = feed.ID and item_category.ITEM_ID = item.ID" //
				+ " and category.ID = item_category.CATEGORIES_ID" //
				+ " order by category.NAME asc";
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
		Query query = em.createNamedQuery("findFeedByUrl");
		query.setParameter("url", url);
		return getSingleResult(query);
	}

	public Feed findLastUpdatedFeed() {
		Query query = em.createNamedQuery("findLastUpdatedFeed");
		return getSingleResult(query);
	}

	public Map<String, Long> getNumFeedType() {
		Map<String, Long> maps = new HashMap<String, Long>();
		Query query = em
				.createNativeQuery("select FEEDTYPE, count(*) from FEED group by FEEDTYPE order by FEEDTYPE ASC");
		List list = query.getResultList();
		for (Object aList : list) {
			Object[] value = (Object[]) aList;
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

	public Feed store(Feed feed) {
		if (feed.getId() == null) {
			em.persist(feed);
		} else {
			feed.setPublicationRatio(tendencyService.computeDayTendency(feed));
			feed = em.merge(feed);
		}
		em.flush();
		return feed;
	}

	public void deleteAllFeeds() {
		Query query = em.createNamedQuery("findAllFeeds");
		List<Feed> feeds = query.getResultList();
		for (Feed feed : feeds) {
			delete(feed);
		}
	}

}
