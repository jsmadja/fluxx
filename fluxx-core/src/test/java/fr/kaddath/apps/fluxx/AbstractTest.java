package fr.kaddath.apps.fluxx;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.cache.RssItemCache;
import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.CategoryService;
import fr.kaddath.apps.fluxx.service.CrawlerService;
import fr.kaddath.apps.fluxx.service.CustomFeedService;
import fr.kaddath.apps.fluxx.service.DownloadableItemService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemBuilderService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import fr.kaddath.apps.fluxx.service.RssService;

public abstract class AbstractTest {

	protected static EJBContainer container;
	protected static Context namingContext;
	protected static FeedService feedService;
	protected static RssService rssService;
	protected static ItemService itemService;
	protected static OpmlService opmlService;
	protected static FeedFetcherService feedFetcherService;
	protected static RssFeedCache cache;
	protected static RssItemCache rssItemCache;
	protected static CustomFeedService customFeedService;
	protected static CategoryService categoryService;
	protected static ItemBuilderService itemBuilderService;
	protected static CrawlerService crawlerService;
	protected static DownloadableItemService downloadableItemService;

	protected static String uuid = UUID.randomUUID().toString();
	protected static HttpServletRequest request;
	protected static int serverPort = 8080;
	protected static String serverName = "fluxx.fr.cr";
	protected static String contextPath = "/fluxx";

	private static final SyndFeedInput syndFeedInput = new SyndFeedInput();

	protected static final String VALID_URL = "http://feeds2.feedburner.com/Frandroid";
	protected static final String VALID_LINK = "http://www.google.fr";
	protected static final String VALID_TITLE = "My title";
	protected static final String TYPE = "audio/mp3";
	protected static final long FILE_LENGTH = 5;
	protected static final String CATEGORY_NAME = "category";

	private static final File FILE_CASTCODEURS = new File("src/test/resources/lescastcodeurs.xml");
	public static final String URL_CASTCODEURS = "http://lescastcodeurs.libsyn.com/rss";

	private static final File FILE_FRANDROID = new File("src/test/resources/frandroid.xml");
	public static final String URL_FRANDROID = "http://feeds2.feedburner.com/Frandroid";

	private static final File FILE_LE_MONDE = new File("src/test/resources/lemonde.xml");
	public static final String URL_LE_MONDE = "http://www.lemonde.fr/rss/sequence/0,2-3546,1-0,0.xml";

	protected static boolean isIntegrationTest;

	private static Object lookup(String key) throws NamingException {
		return namingContext.lookup("java:global/classes/" + key);
	}

	static {
		try {
			isIntegrationTest = "true".equals(System.getenv("integration-test"))
					|| "true".equals(System.getProperty("integration-test"));
			System.err.println("Integration Test : " + isIntegrationTest);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(EJBContainer.MODULES, new File("target/classes"));
			container = EJBContainer.createEJBContainer(properties);

			namingContext = container.getContext();

			cache = (RssFeedCache) lookup("RssFeedCache");

			feedService = (FeedService) lookup("FeedService");
			rssService = (RssService) lookup("RssService");
			itemService = (ItemService) lookup("ItemService");
			opmlService = (OpmlService) lookup("OpmlService");
			feedFetcherService = (FeedFetcherService) lookup("FeedFetcherService");
			itemBuilderService = (ItemBuilderService) lookup("ItemBuilderService");
			customFeedService = (CustomFeedService) lookup("CustomFeedService");
			categoryService = (CategoryService) lookup("CategoryService");
			crawlerService = (CrawlerService) lookup("CrawlerService");
			downloadableItemService = (DownloadableItemService) lookup("DownloadableItemService");

			request = mock(HttpServletRequest.class);
			when(request.getServerPort()).thenReturn(serverPort);
			when(request.getServerName()).thenReturn(serverName);
			when(request.getContextPath()).thenReturn(contextPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Feed createFeedFromUrl(String url) {
		try {
			Feed feed = feedService.findFeedByUrl(url);
			if (feed != null) {
				return feed;
			}
			feed = feedFetcherService.addNewFeed(url);
			return feed;
		} catch (DownloadFeedException ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("deprecation")
	private Feed createFeed(File file) {
		try {
			Feed feed = new Feed();
			feed.setUrl(file.toURL().toString());
			SyndFeed syndFeed = syndFeedInput.build(file);
			feed = feedFetcherService.createFromSyndFeed(feed, syndFeed);
			return feed;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Feed createFeedWithDownloadableItems() {
		if (isIntegrationTest) {
			return createFeedFromUrl(URL_CASTCODEURS);
		} else {
			return createFeed(FILE_CASTCODEURS);
		}
	}

	protected Feed createFeedWithCategories() {
		if (isIntegrationTest) {
			return createFeedFromUrl(URL_FRANDROID);
		} else {
			return createFeed(FILE_FRANDROID);
		}
	}

	protected Feed createFeedLeMonde() {
		if (isIntegrationTest) {
			return createFeedFromUrl(URL_LE_MONDE);
		} else {
			return createFeed(FILE_LE_MONDE);
		}
	}

	protected String createRandomString() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	protected CustomFeed createCustomFeed() {
		return customFeedService.addCustomFeed(createRandomString(), createRandomString(), 7);
	}

	protected Category createCategory() {
		return categoryService.create(createRandomString());
	}

	protected CustomFeed createCustomFeedWithOneFeed(Feed feed) throws DownloadFeedException {
		String customFeedName = feed.getTitle();
		int numDays = 120;
		CustomFeed customFeed = customFeedService.addCustomFeed("fluxxer", customFeedName, numDays);
		customFeed = customFeedService.addFeed(customFeed, feed);
		return customFeed;
	}

	protected void createFeeds() throws FileNotFoundException {
		int maxFeeds = 5;
		Scanner sc = new Scanner(new File("src/test/resources/feeds.urls"));
		int i = 0;
		while (sc.hasNextLine() && i < maxFeeds) {
			String url = sc.nextLine();
			createFeedFromUrl(url);
			i++;
		}

	}

}
