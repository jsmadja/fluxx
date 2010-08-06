package fr.kaddath.apps.fluxx.controller;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;

@Named(value = "opmlImporter")
@ApplicationScoped
public class OpmlImporterBean {

    private String content;
    @Inject
    private OpmlService opmlService;
    @Inject
    private FeedService feedService;
    @Inject
    private FeedFetcherService feedFetcherService;

    private static final Logger LOG = Logger.getLogger("fluxx");

    public String getImportOpml() {
        List<Feed> feeds = opmlService.loadOpml(getContent().getBytes());
        for (Feed feed : feeds) {
            if (null == feedService.findFeedByUrl(feed.getUrl())) {
                try {
                    feedFetcherService.fetch(feed);
                } catch (DownloadFeedException ex) {
                    java.util.logging.Logger.getLogger(OpmlImporterBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                LOG.info(feed.getUrl()+" already exists in database");
            }
        }
        return feeds.size() + " feeds found!";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
