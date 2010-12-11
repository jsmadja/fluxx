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

package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.cache.CustomFeedCache;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.metamodel.CustomFeed_;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
@Interceptors({ChronoInterceptor.class})
@SuppressWarnings("unchecked")
public class CustomFeedService {

    @PersistenceContext
    EntityManager em;

    @EJB
    private ItemService itemService;

    private CriteriaBuilder cb;

    @EJB
    CustomFeedCache feedCache;

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

    public CustomFeed addCustomFeed(String username, String category, int numLastDay) {
        CustomFeed customFeed = new CustomFeed();
        customFeed.setCategory(category);
        customFeed.setUsername(username);
        customFeed.setNumLastDay(numLastDay);
        System.err.println(customFeed + " em:" + em);
        customFeed = store(customFeed);
        return customFeed;
    }

    private CustomFeed store(CustomFeed customFeed) {
        if (customFeed.getId() == null || findById(customFeed.getId()) == null) {
            em.persist(customFeed);
        } else {
            customFeed = em.merge(customFeed);
        }
        em.flush();
        return customFeed;
    }

    public CustomFeed update(CustomFeed customFeed) {
        customFeed = store(customFeed);
        feedCache.remove(customFeed.getId());
        return customFeed;
    }

    public void delete(CustomFeed customFeed) {
        feedCache.remove(customFeed.getId());
        em.remove(findById(customFeed.getId()));
        em.flush();
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

    public void deleteAllCustomFeeds() {
        Query query = em.createQuery("select cf from CustomFeed cf");
        List<CustomFeed> customFeeds = query.getResultList();
        for (CustomFeed customFeed : customFeeds) {
            delete(customFeed);
        }
    }
}
