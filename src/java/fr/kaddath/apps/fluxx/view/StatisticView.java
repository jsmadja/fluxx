package fr.kaddath.apps.fluxx.view;

import fr.kaddath.apps.fluxx.service.AggregatedFeedService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.UserService;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Named(value="statisticView")
@ApplicationScoped
public class StatisticView {

    @Inject
    FeedService feedService;

    @Inject
    ItemService itemService;

    @Inject
    UserService userService;

    @Inject
    AggregatedFeedService aggregatedFeedService;

    public long getNumFeeds() {
        return feedService.getNumFeeds();
    }

    public long getNumItems() {
        return itemService.getNumItems();
    }

    public long getNumFluxxers() {
        return userService.getNumFluxxers();
    }

    public long getNumAggregatedFeeds() {
        return aggregatedFeedService.getNumAggregatedFeeds();
    }
}
