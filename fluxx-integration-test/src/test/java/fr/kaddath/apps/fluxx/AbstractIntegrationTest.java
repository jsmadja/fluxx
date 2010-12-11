package fr.kaddath.apps.fluxx;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import fr.kaddath.apps.fluxx.service.AsynchronousFeedDownloaderService;
import fr.kaddath.apps.fluxx.service.CrawlerService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ScheduledUpdateService;

public abstract class AbstractIntegrationTest {

	protected static EJBContainer container;
	protected static Context namingContext;
	protected static FeedFetcherService feedFetcherService;
	protected static FeedService feedService;
	protected static CrawlerService crawlerService;
	protected static AsynchronousFeedDownloaderService asynchronousFeedDownloaderService;
	protected static ScheduledUpdateService scheduledUpdateService;

	protected static final String VALID_URL = "http://feeds2.feedburner.com/Frandroid";
	protected static final String VALID_LINK = "http://www.google.fr";
	protected static final String VALID_TITLE = "My title";
	protected static final String TYPE = "audio/mp3";
	protected static final long FILE_LENGTH = 5;
	protected static final String CATEGORY_NAME = "category";

	public static final String URL_CASTCODEURS = "http://lescastcodeurs.libsyn.com/rss";
	public static final String URL_FRANDROID = "http://feeds2.feedburner.com/Frandroid";
	public static final String URL_LE_MONDE = "http://www.lemonde.fr/rss/sequence/0,2-3546,1-0,0.xml";

	private static Object lookup(String key) throws NamingException {
		return namingContext.lookup("java:global/classes/" + key);
	}

	static {
		try {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(EJBContainer.MODULES, new File[] { new File("../fluxx-core/target/classes") });
			container = EJBContainer.createEJBContainer(properties);
			namingContext = container.getContext();

			feedFetcherService = (FeedFetcherService) lookup("FeedFetcherService");
			feedService = (FeedService) lookup("FeedService");
			crawlerService = (CrawlerService) lookup("CrawlerService");
			asynchronousFeedDownloaderService = (AsynchronousFeedDownloaderService) lookup("AsynchronousFeedDownloaderService");
			scheduledUpdateService = (ScheduledUpdateService) lookup("ScheduledUpdateService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}