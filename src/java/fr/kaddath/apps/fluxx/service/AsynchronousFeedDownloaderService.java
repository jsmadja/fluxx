package fr.kaddath.apps.fluxx.service;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import java.net.URL;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

@Stateless
public class AsynchronousFeedDownloaderService {

    @Asynchronous
    public Future<Object[]> downloadFeedContent(Feed feed) throws DownloadFeedException {
        try {
            URL feedUrl = new URL(feed.getUrl());
            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
            return new AsyncResult<Object[]>(new Object[]{feed, syndFeed});
        } catch (Throwable ex) {
            throw new DownloadFeedException(ex.getMessage());
        }
    } 
}
