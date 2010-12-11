package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Date;

@Stateless
@Interceptors(ChronoInterceptor.class)
public class TendencyService {

    @EJB
    ItemService itemService;

    public static final int DAYS_IN_A_WEEK = 7;
    public static final int DAYS_IN_A_MONTH = 30;
    public static final int DAYS_IN_A_YEAR = 365;
    public static final int MILLISEC_IN_A_DAY = 1000 * 60 * 60 * 24;

    public double computeDayTendency(Feed feed) {
        long numItems = itemService.getNumItemsOfFeed(feed);
        if (numItems == 0) {
            return 0;
        }
        Item item = itemService.findFirstItemOfFeed(feed);
        return computeDayTendency(item.getPublishedDate(), numItems);
    }

    public double computeWeekTendency(Feed feed) {
        long numItems = itemService.getNumItemsOfFeed(feed);
        if (numItems == 0) {
            return 0;
        }
        Item item = itemService.findFirstItemOfFeed(feed);
        return computeWeekTendency(item.getPublishedDate(), numItems);
    }

    private double computeTendency(long numItems, Date date) {
        int numDays = getNumDaysSince(date);
        if (numDays == 0) {
            numDays = Integer.MAX_VALUE;
        }
        return round((float) (numItems) / numDays, 1);
    }

    double computeDayTendency(Date date, long numItems) {
        return computeTendency(numItems, date);
    }

    double computeWeekTendency(Date date, long numItems) {
        return computeTendency(numItems * DAYS_IN_A_WEEK, date);
    }

    private double round(double what, int howmuch) {
        return ((int) (what * Math.pow(10, howmuch) + .5)) / Math.pow(10, howmuch);
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    private int getNumDaysSince(Date date) {
        long t = System.currentTimeMillis();
        if (date == null) {
            return 0;
        }
        long time = date.getTime();
        return (int) (((float) (t - time)) / MILLISEC_IN_A_DAY);
    }
}
