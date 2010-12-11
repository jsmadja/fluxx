package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static org.mockito.Mockito.mock;

public class TendencyServiceTest {

    private final TendencyService tendencyService = new TendencyService();

    public TendencyServiceTest() {
        ItemService itemService = mock(ItemService.class);
        tendencyService.setItemService(itemService);
    }

    @Test
    public void should_return_0_when_there_is_no_item() {
        Feed feed = new Feed();
        double tendency = tendencyService.computeDayTendency(feed);
        Assert.assertEquals(0D, tendency);
    }

    @Test
    public void should_return_1_when_there_is_one_item_published_yesterday() {
        Date yesterday = new DateTime().minusDays(1).toDate();
        double tendency = tendencyService.computeDayTendency(yesterday, 1);
        Assert.assertEquals(1D, tendency);
    }

    @Test
    public void should_return_3_when_there_are_3_items_published_yesterday() {
        Date yesterday = new DateTime().minusDays(1).toDate();
        double tendency = tendencyService.computeDayTendency(yesterday, 3);
        Assert.assertEquals(3D, tendency);
    }

    @Test
    public void should_return_05_when_there_are_1_item_published_in_two_days() {
        Date twoDayBefore = new DateTime().minusDays(2).toDate();
        double tendency = tendencyService.computeDayTendency(twoDayBefore, 1);
        Assert.assertEquals(0.5, tendency);
    }

}
