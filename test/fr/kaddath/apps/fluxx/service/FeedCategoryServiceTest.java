package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class FeedCategoryServiceTest extends AbstractTest {

    @Test
    public void add() {
        createCategory();
    }

    @Test
    public void findNumItemFeedsByCategory() throws Exception {
        createFeed();
        List<String[]> findNumItemFeedsByCategory = feedCategoryService.findNumItemFeedsByCategory();
        assertNotNull(findNumItemFeedsByCategory);
        assertTrue(findNumItemFeedsByCategory.size() > 0);
    }

    @Test
    public void findCategoryByName() throws Exception {
        FeedCategory category = createCategory();
        FeedCategory categoryFound = feedCategoryService.findCategoryByName(category.getName());
        assertNotNull(categoryFound);
        assertEquals(category.getName(), categoryFound.getName());
    }

    @Test
    public void findCategoryByNameWithLike() throws Exception {
        FeedCategory category = createCategory();
        String shortName = category.getName().substring(1);
        List<String> categories = feedCategoryService.findCategoryNamesWithLike(shortName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

    @Test
    public void findCategoryNamesInLowerCaseWithLike() throws Exception {   
        FeedCategory category = createCategory();
        String shortName = category.getName().substring(1);
        List<String> categories = feedCategoryService.findCategoryNamesInLowerCaseWithLike(shortName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

}