/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.kaddath.apps.fluxx;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

public class FluxxTest extends AbstractTest {

    private static String uuid = UUID.randomUUID().toString();

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
    public void addAggregatedFeed() {
        Feed feed = feedService.findAllFeeds().get(0);
        Fluxxer user = userService.findAll().get(0);

        AggregatedFeed aggregatedFeed = new AggregatedFeed();
        aggregatedFeed.setNumLastDay(7);
        aggregatedFeed.setFeeds(new ArrayList<Feed>());
        aggregatedFeed.setAggregatedFeedId(UUID.randomUUID().toString());
        aggregatedFeed.setName("name");
        aggregatedFeed.setFluxxer(user);
        aggregatedFeed.getFeeds().add(feed);

        user.getAggregatedFeeds().add(aggregatedFeed);
        user = userService.update(user);
        
        assertTrue(user.getAggregatedFeeds().size()>0);
    }

    @Test
    public void buildAggregatedFeedRss() {
        Fluxxer user = userService.findByUsername(uuid);
        List<AggregatedFeed> aggregatedFeeds = user.getAggregatedFeeds();

        for (AggregatedFeed af:aggregatedFeeds) {
            try {
                String xml = rssService.createRssFeed(af, "http://localhost:8080/fluxx");
                System.err.println(xml);
                assertNotNull(xml);
                assertTrue(xml.contains("fluxx"));
            } catch (Exception ex) {
                fail(ex.getMessage());
            }
        }
    }
}