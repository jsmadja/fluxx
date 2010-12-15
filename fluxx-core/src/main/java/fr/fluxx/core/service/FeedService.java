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
import javax.persistence.TypedQuery;
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

	private static final int MAX_FEEDS_TO_RETRIEVE = 100;

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
		Query query = em.createQuery(cq);
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		return query.getResultList();
	}

	public List<Feed> findAllTopPriorityFeeds() {
		Query query = em.createNamedQuery("findAllTopPriorityFeeds");
		return query.getResultList();
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
		Query query = em.createNamedQuery("findFeedsByItemTitle");
		query.setParameter(1, '%'+filter.toLowerCase()+'%');
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public List<Feed> findFeedsByCategory(String filter) {
		Query query = em.createNamedQuery("findFeedsByCategory");
		query.setParameter(1, '%'+filter.toLowerCase()+'%');
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public List<Feed> findFeedsByDescriptionUrlAuthorTitle(String filter) {
		filter = filter.toLowerCase();
		Query query = em.createNamedQuery("findFeedsByDescriptionUrlAuthorTitle");
		for (int i=1; i<5; i++)
			query.setParameter(i, '%'+filter+'%');
		query.setMaxResults(MAX_FEEDS_TO_RETRIEVE);
		List<Long> feedIds = query.getResultList();
		return fromFeedIdListToFeedList(feedIds);
	}

	public Long getNumFeeds() {
		Query query = em.createNamedQuery("getNumFeeds");
		return (Long) query.getSingleResult();
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
		TypedQuery<Object[]> query = em.createNamedQuery("getNumFeedType", Object[].class);
		List<Object[]> list = query.getResultList();
		for (Object[] value : list) {
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
