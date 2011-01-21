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

package fr.fluxx.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import fr.fluxx.core.cache.CustomFeedCache;
import fr.fluxx.core.cache.RssItemCache;
import fr.fluxx.core.domain.Category;
import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.DownloadableItem;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.exception.DownloadFeedException;
import fr.fluxx.core.service.CategoryService;
import fr.fluxx.core.service.CustomFeedService;
import fr.fluxx.core.service.DownloadableItemService;
import fr.fluxx.core.service.FeedFetcherService;
import fr.fluxx.core.service.FeedService;
import fr.fluxx.core.service.ItemBuilderService;
import fr.fluxx.core.service.ItemService;
import fr.fluxx.core.service.OpmlService;
import fr.fluxx.core.service.RssService;

public abstract class AbstractTest {

	protected static EJBContainer container;
	protected static Context namingContext;
	protected static FeedService feedService;
	protected static RssService rssService;
	protected static ItemService itemService;
	protected static OpmlService opmlService;
	protected static FeedFetcherService feedFetcherService;
	protected static CustomFeedCache cache;
	protected static RssItemCache rssItemCache;
	protected static CustomFeedService customFeedService;
	protected static CategoryService categoryService;
	protected static ItemBuilderService itemBuilderService;
	protected static DownloadableItemService downloadableItemService;

	protected static String uuid = UUID.randomUUID().toString();
	protected static HttpServletRequest request;
	protected static int serverPort = 8080;
	protected static String serverName = "fluxx.fr.cr";
	protected static String contextPath = "/fluxx";

	private static final SyndFeedInput syndFeedInput = new SyndFeedInput();

	public static final String VALID_URL = "http://feeds2.feedburner.com/Frandroid";
	protected static final String VALID_LINK = "http://www.google.fr";
	protected static final String VALID_TITLE = "My title";
	protected static final String TYPE = "audio/mp3";
	protected static final long FILE_LENGTH = 5;
	protected static final String CATEGORY_NAME = "category";

	private static final File FILE_CASTCODEURS = new File("src/test/resources/lescastcodeurs.xml");
	private static final File FILE_FRANDROID = new File("src/test/resources/frandroid.xml");
	private static final File FILE_LE_MONDE = new File("src/test/resources/lemonde.xml");

	private static Object lookup(String key) throws NamingException {
		return namingContext.lookup("java:global/classes/" + key);
	}

	static {
		try {
			long initialTime = System.currentTimeMillis();
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(EJBContainer.MODULES, new File("target/classes"));
			container = EJBContainer.createEJBContainer(properties);

			namingContext = container.getContext();

			cache = (CustomFeedCache) lookup("CustomFeedCache");

			feedService = (FeedService) lookup("FeedService");
			rssService = (RssService) lookup("RssService");
			itemService = (ItemService) lookup("ItemService");
			opmlService = (OpmlService) lookup("OpmlService");
			feedFetcherService = (FeedFetcherService) lookup("FeedFetcherService");
			itemBuilderService = (ItemBuilderService) lookup("ItemBuilderService");
			customFeedService = (CustomFeedService) lookup("CustomFeedService");
			categoryService = (CategoryService) lookup("CategoryService");
			downloadableItemService = (DownloadableItemService) lookup("DownloadableItemService");

			long time = System.currentTimeMillis();
			System.err.println("Initialization time: "+(time-initialTime)+" ms");
			
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

	private static final Map<File, SyndFeed> syndFeedcache = new HashMap<File, SyndFeed>();

	@SuppressWarnings("deprecation")
	private Feed createFeed(File file) {
		try {
			Feed feed = new Feed();
			feed.setUrl(file.toURL().toString());

			SyndFeed syndFeed;
			if (syndFeedcache.containsKey(file)) {
				syndFeed = syndFeedcache.get(file);
			} else {
				syndFeed = syndFeedInput.build(file);
				syndFeedcache.put(file, syndFeed);
			}
			feed = feedFetcherService.createFromSyndFeed(feed, syndFeed);
			feed = feedService.store(feed);
			return feed;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Feed createFeedWithDownloadableItems() {
		return createFeed(FILE_CASTCODEURS);
	}

	protected Feed createFeedWithCategories() {
		return createFeed(FILE_FRANDROID);
	}

	protected Feed createFeedLeMonde() {
		return createFeed(FILE_LE_MONDE);
	}

	protected String createRandomString() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	protected CustomFeed createCustomFeed() {
		return customFeedService.addCustomFeed(createRandomString(), createRandomString(), 7);
	}

	protected Category createCategory() {
		return categoryService.store(new Category(createRandomString()));
	}

	protected CustomFeed createCustomFeedWithOneFeed(Feed feed) {
		String customFeedName = feed.getTitle();
		int numDays = 120;
		CustomFeed customFeed = customFeedService.addCustomFeed("fluxxer", customFeedName, numDays);
		customFeed = customFeedService.addFeed(customFeed, feed);
		return customFeed;
	}

	protected Feed createCompleteFeed() {
		Feed feed = new Feed();
		feed.setUrl(VALID_URL);
		feed.setTitle(VALID_TITLE);
		feed.setPublishedDate(new Date());

		Item item = new Item(VALID_LINK, VALID_TITLE, feed, new Date());
		
		Set<DownloadableItem> downloadableItems = new HashSet<DownloadableItem>();
		DownloadableItem downloadableItem = new DownloadableItem(VALID_LINK);
		downloadableItem.setFileLength(FILE_LENGTH);
		downloadableItem.setType(TYPE);
		downloadableItem = downloadableItemService.store(downloadableItem);
		downloadableItems.add(downloadableItem);
		item.setDownloadableItems(downloadableItems);

		Set<Category> categories = new HashSet<Category>();
		Category category = new Category(CATEGORY_NAME);
		category = categoryService.store(category);
		categories.add(category);

		item.setCategories(categories);

		feed.getItems().add(item);
		return feed;
	}

	@After
	public void tearDown() {
		deleteAll();
	}

	protected void deleteAll() {
		customFeedService.deleteAllCustomFeeds();
		itemService.deleteAllItems();
		categoryService.deleteAllCategories();
		downloadableItemService.deleteAllDownloadableItems();
		feedService.deleteAllFeeds();
	}

}
