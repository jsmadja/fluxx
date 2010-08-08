package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class FeedCategoryServiceTest extends AbstractTest {

    @Test
    public void findNumItemFeedsByCategory() throws Exception {
        List<String[]> findNumItemFeedsByCategory = feedCategoryService.findNumItemFeedsByCategory();
        assertNotNull(findNumItemFeedsByCategory);
    }

    @Test
    public void findCategoryByName() throws Exception {
        List<String> categories = feedCategoryService.findCategoryNamesWithLike("", 1);
        System.out.println(categories.get(0));
        FeedCategory findCategoryByName = feedCategoryService.findCategoryByName(categories.get(0));
        assertNotNull(findCategoryByName);
    }

    @Test
    public void findCategoryByNameWithLike() throws Exception {
        List<String> categories = feedCategoryService.findCategoryNamesWithLike("", 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

    @Test
    public void findCategoryNamesInLowerCaseWithLike() throws Exception {
        List<String> categories = feedCategoryService.findCategoryNamesInLowerCaseWithLike("", 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

}