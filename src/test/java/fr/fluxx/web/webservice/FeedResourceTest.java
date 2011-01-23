package fr.fluxx.web.webservice;

import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.AbstractTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class FeedResourceTest extends AbstractTest {

    @Test
    public void should_create_a_valid_xml() throws Exception {
        FeedResource feedResource = new FeedResource();
        feedResource.setRssService(rssService);
        feedResource.setRequest(request);

        CustomFeed feed = createCustomFeedWithOneFeed(createFeedWithDownloadableItems());

        String xml = feedResource.getXml(feed.getUsername(), feed.getCategory());
        assertNotNull(xml);
    }

}