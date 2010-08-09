package fr.kaddath.apps.fluxx.view;

import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.service.AggregatedFeedService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.UserService;
import java.util.Calendar;
import java.util.Date;
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

    public String getNumItemsByDayOfLastMonth() {
        return getNumItemsBetween(firstDayOfLastMonth(), firstDayOfCurrentMonth());
    }

    public String getNumItemsByDayOfCurrentMonth() {
        return getNumItemsBetween(firstDayOfCurrentMonth(), Calendar.getInstance());
    }

    public long getMaxNumItemsOfLastMonth() {
        return getMaxNumItemsBetween(firstDayOfLastMonth(), firstDayOfCurrentMonth());
    }

    public long getMaxNumItemsOfCurrentMonth() {
        return getMaxNumItemsBetween(firstDayOfCurrentMonth(), Calendar.getInstance());
    }

    /**
     * [1, 1.2, 1.7, 1.5, .7, .3]
     * @return
     */
    private String getNumItemsBetween(Calendar beginDate, Calendar endDate) {
        StringBuilder sb = new StringBuilder("[");
        do {
            long numItemsByDay = itemService.getNumItemsByDay(beginDate.getTime());
            sb.append(numItemsByDay).append(",");
            beginDate.add(Calendar.DAY_OF_YEAR, 1);
        } while (beginDate.before(endDate));
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    private long getMaxNumItemsBetween(Calendar beginDate, Calendar endDate) {
        long max = 0;
        do {
            long numItemsByDay = itemService.getNumItemsByDay(beginDate.getTime());
            max = Math.max(max, numItemsByDay);
            beginDate.add(Calendar.DAY_OF_YEAR, 1);
        } while (beginDate.before(endDate));
        return max;
    }


    private Calendar firstDayOfLastMonth() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        return date;
    }

    private Calendar firstDayOfCurrentMonth() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        return date;
    }
}