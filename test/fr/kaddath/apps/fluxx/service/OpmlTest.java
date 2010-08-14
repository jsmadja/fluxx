package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Opml;
import fr.kaddath.apps.fluxx.domain.Feed;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class OpmlTest {

    @Test
    public void opml() {
        List<Feed> feeds = new ArrayList<Feed>();
        Feed feed = new Feed();
        feed.setTitle("titre");
        feed.setUrl("http://www.google.fr");
        feeds.add(feed);

        String xmlOpml = new Opml(feeds).build();
        System.err.println(xmlOpml);
        assertTrue(StringUtils.isNotEmpty(xmlOpml));        
    }

}