package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.CustomFeed_;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@Interceptors({ ChronoInterceptor.class })
public class CustomFeedService {

	@PersistenceContext
	EntityManager em;

	@EJB
	private ItemService itemService;

	private CriteriaBuilder cb;

	@EJB
	RssFeedCache feedCache;

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
			items.addAll(itemService.findItemsByFeedAndAfterDate(f, date));
		}

		return items;
	}

	public CustomFeed addCustomFeed(String username, String category, int numLastDay) {
		CustomFeed customFeed = new CustomFeed();
		customFeed.setCategory(category);
		customFeed.setUsername(username);
		customFeed.setNumLastDay(numLastDay);
		customFeed = em.merge(customFeed);
		em.flush();
		return customFeed;
	}

	public CustomFeed update(CustomFeed customFeed) {
		customFeed = em.merge(customFeed);
		em.flush();
		feedCache.remove(customFeed.getId().toString());
		return customFeed;
	}

	public void delete(CustomFeed customFeed) {
		em.remove(findById(customFeed.getId()));
	}

	public Long getNumCustomFeeds() {
		Query query = em.createQuery("select count(customFeed) from CustomFeed customFeed");
		return (Long) query.getSingleResult();
	}

	public String createUrl(HttpServletRequest request, CustomFeed feed) {
		int port = request.getServerPort();
		String server = request.getServerName();
		String contextRoot = request.getContextPath();
		return "http://" + server + ":" + port + contextRoot + "/rss?id=" + feed.getId();
	}

	public CustomFeed addFeed(CustomFeed customFeed, Feed feed) {
		if (customFeed.getFeeds() == null) {
			customFeed.setFeeds(new ArrayList<Feed>());
		}
		customFeed.getFeeds().add(feed);
		customFeed = update(customFeed);
		return customFeed;
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
}
