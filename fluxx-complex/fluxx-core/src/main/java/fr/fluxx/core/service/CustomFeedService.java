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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import fr.fluxx.core.cache.CustomFeedCache;
import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.domain.metamodel.CustomFeed_;
import fr.fluxx.core.interceptor.ChronoInterceptor;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors({ChronoInterceptor.class})
public class CustomFeedService {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private ItemService itemService;

	private CriteriaBuilder cb;

	@EJB
	private CustomFeedCache feedCache;

	@PostConstruct
	public void init() {
		cb = em.getCriteriaBuilder();
	}

	public CustomFeed findById(Long id) {
		return em.find(CustomFeed.class, id);
	}

	public List<Item> findItemsByCustomFeed(CustomFeed customFeed) {
		List<Item> items = new ArrayList<Item>();
		for (Feed f : customFeed.getFeeds()) {
			items.addAll(itemService.findItemsByFeed(f));
		}
		return items;
	}

	public List<Item> findItemsOfLastDaysByCustomFeed(CustomFeed customFeed) {
		Calendar calendar = Calendar.getInstance();

		if (customFeed.getNumLastDay() != null) {
			calendar.add(Calendar.DAY_OF_YEAR, -customFeed.getNumLastDay());
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}

		Date date = calendar.getTime();

		List<Item> items = new ArrayList<Item>();
		for (Feed f : customFeed.getFeeds()) {
			items.addAll(itemService.findItemsOfFeedAndAfterDate(f, date));
		}

		return items;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public CustomFeed addCustomFeed(String username, String category, int numLastDay) {
		CustomFeed customFeed = new CustomFeed();
		customFeed.setCategory(category);
		customFeed.setUsername(username);
		customFeed.setNumLastDay(numLastDay);
		customFeed = store(customFeed);
		return customFeed;
	}

	private CustomFeed store(final CustomFeed customFeed) {
		CustomFeed storedCustomFeed;
		if (customFeed.getId() == null || findById(customFeed.getId()) == null) {
			em.persist(customFeed);
			storedCustomFeed = customFeed;
		} else {
			storedCustomFeed = em.merge(customFeed);
		}
		em.flush();
		return storedCustomFeed;
	}

	public CustomFeed update(final CustomFeed customFeed) {
		CustomFeed storedCustomFeed = store(customFeed);
		feedCache.remove(storedCustomFeed.getId());
		return storedCustomFeed;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(CustomFeed customFeed) {
		feedCache.remove(customFeed.getId());
		em.remove(findById(customFeed.getId()));
		em.flush();
	}

	public Long getNumCustomFeeds() {
		Query query = em.createNamedQuery("getNumCustomFeeds");
		return (Long) query.getSingleResult();
	}

	public String createUrl(HttpServletRequest request, CustomFeed feed) {
		int port = request.getServerPort();
		String server = request.getServerName();
		String contextRoot = request.getContextPath();
		return "http://" + server + ":" + port + contextRoot + "/rss?id=" + feed.getId();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public CustomFeed addFeed(final CustomFeed customFeed, Feed feed) {
		if (customFeed.getFeeds() == null) {
			customFeed.setFeeds(new ArrayList<Feed>());
		}
		customFeed.getFeeds().add(feed);
		return update(customFeed);
	}

	public CustomFeed findByUsernameAndName(String username, String category) {
		CriteriaQuery<CustomFeed> cq = cb.createQuery(CustomFeed.class);
		Root<CustomFeed> feed = cq.from(CustomFeed.class);

		Predicate usernameClause = cb.equal(feed.get(CustomFeed_.username), username);
		Predicate categoryClause = cb.equal(feed.get(CustomFeed_.category), category);
		cq.where(cb.and(usernameClause, categoryClause));
		cq.select(feed);
		List<CustomFeed> list = em.createQuery(cq).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteAllCustomFeeds() {
		Query query = em.createNamedQuery("deleteAllCustomFeeds");
		query.executeUpdate();
		em.flush();
	}
}
