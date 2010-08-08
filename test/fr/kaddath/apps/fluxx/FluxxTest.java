package fr.kaddath.apps.fluxx;

import com.sun.syndication.io.FeedException;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class FluxxTest extends AbstractTest {

    @Test
    public void addUser() {
        Fluxxer user = new Fluxxer();
        user.setUsername(uuid);
        user.setPassword("password");
        user.setEmail(user.getUsername()+"@gmail.com");
        userService.persist(user);
    }

    @Test
    public void addFeed() throws DownloadFeedException {
        String url = "http://fluxx.fr.cr:8080/fluxx/rss?id=-2c0b434f7d6f147dc3ee40fe02703163";
        Feed feed = feedService.findFeedByUrl(url);
        if (feed != null) {
            feedService.delete(feed);
        }
        feed = feedFetcherService.add(url);
        assertNotNull(feed);
    }

    @Test
    public void listUsers() {
        List<Fluxxer> users = userService.findAll();
        assertNotNull(users);
        assertTrue(users.size()>0);
    }

    @Test
    public void getRssFeedById() throws ParseException, IOException, FeedException {
        Fluxxer user = userService.findByUsername(uuid);
        List<AggregatedFeed> aggregatedFeeds = user.getAggregatedFeeds();
        AggregatedFeed af = aggregatedFeeds.get(0);
        String xml = rssService.getRssFeedById(af.getAggregatedFeedId(), request);
        System.err.println(xml);
        assertNotNull(xml);
        assertTrue(xml.contains("fluxx"));        
    }


}