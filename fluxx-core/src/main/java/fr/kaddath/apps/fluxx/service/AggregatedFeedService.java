package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import javax.servlet.http.HttpServletRequest;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.AggregatedFeed_;

@Stateless
public class AggregatedFeedService {

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

	public AggregatedFeed findById(Long id) {
		return em.find(AggregatedFeed.class, id);
	}

	public List<Item> findItemsByAggregatedFeed(AggregatedFeed feed) {
		Calendar calendar = Calendar.getInstance();

		if (feed.getNumLastDay() != null) {
			calendar.add(Calendar.DAY_OF_YEAR, -feed.getNumLastDay());
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}

		Date date = calendar.getTime();

		List<Item> items = new ArrayList<Item>();
		for (Feed f : feed.getFeeds()) {
			items.addAll(itemService.findItemsByFeedAndAfter(f, date));
		}

		return items;
	}

	public AggregatedFeed addAggregatedFeed(String username, String theme, int numLastDay) {
		AggregatedFeed aggregatedFeed = new AggregatedFeed();
		aggregatedFeed.setTheme(theme);
		aggregatedFeed.setUsername(username);
		aggregatedFeed.setNumLastDay(numLastDay);
		aggregatedFeed = em.merge(aggregatedFeed);
		em.flush();
		return aggregatedFeed;
	}

	public AggregatedFeed merge(AggregatedFeed aggregatedFeed) {
		AggregatedFeed af = em.merge(aggregatedFeed);
		feedCache.remove(af.getId().toString());
		return af;
	}

	public void delete(AggregatedFeed aggregatedFeed) {
		em.remove(findById(aggregatedFeed.getId()));
	}

	public Long getNumAggregatedFeeds() {
		Query query = em.createQuery("select count(a) from AggregatedFeed a");
		return (Long) query.getSingleResult();
	}

	public String createUrl(HttpServletRequest request, AggregatedFeed feed) {
		int port = request.getServerPort();
		String server = request.getServerName();
		String contextRoot = request.getContextPath();
		return "http://" + server + ":" + port + contextRoot + "/rss?id=" + feed.getId();
	}

	public AggregatedFeed addFeedToAggregatedFeed(Feed feed, AggregatedFeed aggregatedFeed) {
		if (aggregatedFeed.getFeeds() == null) {
			aggregatedFeed.setFeeds(new ArrayList<Feed>());
		}
		aggregatedFeed.getFeeds().add(feed);
		aggregatedFeed = merge(aggregatedFeed);
		return aggregatedFeed;
	}

	public AggregatedFeed findByUsernameAndName(String username, String theme) {
		CriteriaQuery<AggregatedFeed> cq = cb.createQuery(AggregatedFeed.class);
		Root<AggregatedFeed> feed = cq.from(AggregatedFeed.class);

		Predicate usernameClause = cb.equal(feed.get(AggregatedFeed_.username), username);
		Predicate themeClause = cb.equal(feed.get(AggregatedFeed_.theme), theme);
		cq.where(cb.and(usernameClause, themeClause));
		cq.select(feed);
		List<AggregatedFeed> list = em.createQuery(cq).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
