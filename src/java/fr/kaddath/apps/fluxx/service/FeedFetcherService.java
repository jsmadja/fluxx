package fr.kaddath.apps.fluxx.service;

import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FeedFetcherService {

    private static final Logger LOG = Logger.getLogger("fluxx");
    private static final Logger STACK = Logger.getLogger("fluxx.stack");

    @EJB
    AsynchronousFeedDownloaderService downloaderService;

    @EJB
    ItemService itemService;

    @EJB
    FeedCategoryService feedCategoryService;

    @EJB
    DownloadableItemService downloadableItemService;

    @EJB
    NotificationService notificationService;

    @EJB
    IFeedService feedService;

    @PersistenceContext
    EntityManager em;

    public Feed add(String feedUrl) throws DownloadFeedException {
        Feed feed = new Feed();
        feed.setUrl(feedUrl);
        fetch(feed);
        return em.merge(feed);
    }

    public void fetch(Feed feed) throws DownloadFeedException {
        try {
            URL feedUrl = new URL(feed.getUrl());
            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
            updateWithFeedContent(feed, syndFeed);
        } catch (Exception e) {
            throw new DownloadFeedException(e.getMessage());
        }
    }

    public void update(Feed feed, SyndFeed syndFeed) throws DownloadFeedException {
        updateFeedInformations(feed, syndFeed);
        if (feed.getId() == null) {
            em.persist(feed);
        } else {
            feed = em.merge(feed);
        }
        em.flush();
        updateFeedItems(feed, syndFeed);
    }

    private List<Future<Object[]>> downloadFeeds() {
        List<Future<Object[]>> couples = new ArrayList<Future<Object[]>>();
        List<Feed> feeds = feedService.findAllFeedsNotInError();
        for (Feed feed : feeds) {
            try {
                couples.add(downloaderService.downloadFeedContent(feed));
            } catch (DownloadFeedException ex) {
                Logger.getLogger(FeedService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return couples;
    }

    private void updateFeeds(List<Future<Object[]>> couples) {
        for (int i = 0; i < couples.size();) {
            Future<Object[]> couple = couples.get(i);
            if (couple.isDone()) {
                try {
                    Feed feed = (Feed) couple.get()[0];
                    SyndFeed syndFeed = (SyndFeed) couple.get()[1];
                    LOG.log(Level.INFO, "[{0}%] Update feed : {1}", new Object[]{getInPercent(i, couples.size()), feed.getTitle()});
                    update(feed, syndFeed);
                } catch (Exception ex) {
                    Logger.getLogger(FeedService.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FeedService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void updateWithFeedContent(Feed feed, SyndFeed syndFeed) {
        try {
            feed.setInError(false);
            update(feed, syndFeed);
            em.merge(feed);
        } catch (Throwable e) {
            LOG.warning(e.getMessage());
            feed.setInError(true);
            em.merge(feed);
        }
    }

    @Interceptors({ChronoInterceptor.class})
    @Schedule(minute="*/10", hour="*")
    public void updateAllAsynchronously() {
        List<Future<Object[]>> couples = downloadFeeds();
        updateFeeds(couples);
    }

    private int getInPercent(int counter, float numFeeds) {
        return (int) ((counter / numFeeds) * 100);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(Feed feed) {
        try {
            feed.setInError(false);
            fetch(feed);
            em.merge(feed);
        } catch (Throwable e) {
            LOG.warning(e.getMessage());
            feed.setInError(true);
            em.merge(feed);
        }
    }

    public void notifyUsers(Feed feed, Item item) {
        if (feed.getId() != null) {
            try {
                notificationService.notifyOnNewItem(feed, item);
            } catch (Throwable t) {
                LOG.log(Level.WARNING, "Notification failed : {0}", t.getMessage());
            }
        }
    }

    private void updateFeedInformations(Feed feed, SyndFeed syndFeed) {
        feed.setAuthor(syndFeed.getAuthor());
        if (StringUtils.isEmpty(feed.getAuthor()) && syndFeed.getAuthors() != null && !syndFeed.getAuthors().isEmpty()) {
            SyndPerson person = (SyndPerson) syndFeed.getAuthors().get(0);
            feed.setAuthor(person.getName());
        }
        if (StringUtils.isBlank(feed.getAuthor())) {
            feed.setAuthor(syndFeed.getCopyright());
        }

        feed.setDescription(syndFeed.getDescription());
        feed.setEncoding(syndFeed.getEncoding());
        feed.setFeedType(syndFeed.getFeedType());
        feed.setPublishedDate(syndFeed.getPublishedDate());
        feed.setTitle(syndFeed.getTitle());
    }

    @SuppressWarnings("unchecked")
    private void updateFeedItems(Feed feed, SyndFeed syndFeed) {
        if (mustBeUpdated(syndFeed)) {
            for (Object o : syndFeed.getEntries()) {
                SyndEntryImpl syndEntryImpl = (SyndEntryImpl) o;
                String link = syndEntryImpl.getLink();
                Item feedItem = itemService.findItemsByLink(link);
                if (feedItem == null) {
                    LOG.log(Level.INFO, "New item found : {0}", link);
                    Item item = createItemFromSyndEntry(syndEntryImpl);
                    item.setFeed(feed);
                    em.persist(item);
                    feed.setLastUpdate(Calendar.getInstance().getTime());
                    feed = em.merge(feed);
                    em.flush();
                    notifyUsers(feed, item);
                }
            }
        }
    }

    private Item createItemFromSyndEntry(SyndEntryImpl syndEntryImpl) {
        Item item = new Item();
        addItemInformations(syndEntryImpl, item);
        em.persist(item);
        addFeedCategories(syndEntryImpl, item);
        addDownloadableItems(syndEntryImpl, item);
        return item;
    }

    private void addItemInformations(SyndEntryImpl syndEntryImpl, Item item) {
        item.setAuthor(syndEntryImpl.getAuthor());
        if (syndEntryImpl.getDescription() != null) {
            item.setDescription(syndEntryImpl.getDescription().getValue());
            item.setDescriptionType(syndEntryImpl.getDescription().getType());
        }
        item.setLink(syndEntryImpl.getLink());
        item.setPublishedDate(syndEntryImpl.getPublishedDate());
        item.setTitle(syndEntryImpl.getTitle());
        item.setUpdatedDate(syndEntryImpl.getUpdatedDate());
        item.setUri(syndEntryImpl.getUri());
    }

    @SuppressWarnings("unchecked")
    private void addFeedCategories(SyndEntryImpl syndEntryImpl, Item item) {
        Set<FeedCategory> feedCategories = new HashSet<FeedCategory>();
        List<SyndCategory> syndCategories = syndEntryImpl.getCategories();
        for (SyndCategory syndCategorie : syndCategories) {
            String categoryName = syndCategorie.getName();
            FeedCategory feedCategory = null;
            if (StringUtils.isNotBlank(categoryName)) {
                feedCategory = feedCategoryService.findCategoryByName(categoryName);
                if (feedCategory == null) {
                    feedCategory = new FeedCategory();
                    feedCategory.setName(categoryName);
                    em.persist(feedCategory);
                    em.flush();
                }
                feedCategories.add(feedCategory);
            }
        }
        item.setFeedCategories(feedCategories);
    }

    @SuppressWarnings("unchecked")
    private void addDownloadableItems(SyndEntryImpl syndEntryImpl, Item item) {
        Set<DownloadableItem> downloadableItems = new HashSet<DownloadableItem>();
        List<SyndEnclosure> syndEnclosures = syndEntryImpl.getEnclosures();
        for (SyndEnclosure syndEnclosure : syndEnclosures) {
            DownloadableItem downloadableItem = null;
            String url = syndEnclosure.getUrl();
            if (StringUtils.isNotBlank(url)) {
                downloadableItem = downloadableItemService.findByUrl(url);
            }
            if (downloadableItem == null) {
                downloadableItem = new DownloadableItem();
                downloadableItem.setUrl(url);
                downloadableItem.setFileLength(syndEnclosure.getLength());
                downloadableItem.setType(syndEnclosure.getType());
                downloadableItem.setItem(item);
                em.persist(downloadableItem);
                em.flush();
            }
            downloadableItems.add(downloadableItem);
        }
        item.setDownloadableItems(downloadableItems);
    }

    private boolean mustBeUpdated(SyndFeed syndFeed) {
        SyndEntryImpl syndEntryImpl = (SyndEntryImpl) syndFeed.getEntries().get(0);
        Date publishedDate = syndEntryImpl.getPublishedDate();
        if (publishedDate == null) {
            return true;
        }
        return itemService.findItemsByLink(syndEntryImpl.getLink()) == null;
    }

    public IFeedService getFeedService() {
        return feedService;
    }

    public void setFeedService(IFeedService feedService) {
        this.feedService = feedService;
    }

}
