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
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.AggregatedFeed_;
import fr.kaddath.apps.fluxx.security.CryptographicService;

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
    private void init() {
        cb = em.getCriteriaBuilder();
    }

    public AggregatedFeed findByAggregatedFeedId(String id) {
        CriteriaQuery<AggregatedFeed> cq = cb.createQuery(AggregatedFeed.class);
        Root<AggregatedFeed> feed = cq.from(AggregatedFeed.class);
        cq.where(cb.equal(feed.get(AggregatedFeed_.aggregatedFeedId), id));
        cq.select(feed);
        List<AggregatedFeed> list = em.createQuery(cq).getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
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
        for (Feed f:feed.getFeeds()) {
            items.addAll(itemService.findItemsByFeedAndAfter(f, date));
        }

        return items;
    }

    public Fluxxer addAggregatedFeed(Fluxxer fluxxer, String name, int numLastDay) {
        AggregatedFeed aggregatedFeed = new AggregatedFeed();
        String aggregatedFeedId = CryptographicService.toMD5(name+fluxxer.getUsername());
        aggregatedFeed.setAggregatedFeedId(aggregatedFeedId);
        aggregatedFeed.setName(name);
        aggregatedFeed.setFluxxer(fluxxer);
        aggregatedFeed.setNumLastDay(numLastDay);
        fluxxer.getAggregatedFeeds().add(aggregatedFeed);
        fluxxer = em.merge(fluxxer);
        em.flush();
        return fluxxer;
    }

    public AggregatedFeed merge(AggregatedFeed aggregatedFeed) {
        AggregatedFeed af = em.merge(aggregatedFeed);
        feedCache.remove(af.getAggregatedFeedId());
        return af;
    }

    public void delete(AggregatedFeed aggregatedFeed) {
        em.remove(findByAggregatedFeedId(aggregatedFeed.getAggregatedFeedId()));
    }

    public Long getNumAggregatedFeeds() {
        Query query = em.createQuery("select count(a) from AggregatedFeed a");
        return (Long)query.getSingleResult();
    }

    public String createUrl(HttpServletRequest request, AggregatedFeed feed) {
        int port = request.getServerPort();
        String server = request.getServerName();
        String contextRoot = request.getContextPath();
        return "http://"+server+":"+port+contextRoot+"/rss?id="+feed.getAggregatedFeedId();
    }

    public AggregatedFeed addFeedToAggregatedFeed(Feed feed, AggregatedFeed aggregatedFeed) {
        if (aggregatedFeed.getFeeds() == null) {
            aggregatedFeed.setFeeds(new ArrayList<Feed>());
        }
        aggregatedFeed.getFeeds().add(feed);
        aggregatedFeed = merge(aggregatedFeed);
        return aggregatedFeed;
    }
}
