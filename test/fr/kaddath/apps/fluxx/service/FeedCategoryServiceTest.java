package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class FeedCategoryServiceTest extends AbstractTest {

    private static String categoryName;

    @BeforeClass
    public static void init() {
        categoryName = uuid;
    }

    @Test
    public void add() {
        feedCategoryService.create(categoryName);
    }

    @Test
    public void findNumItemFeedsByCategory() throws Exception {
        List<String[]> findNumItemFeedsByCategory = feedCategoryService.findNumItemFeedsByCategory();
        assertNotNull(findNumItemFeedsByCategory);
    }

    @Test
    public void findCategoryByName() throws Exception {
        FeedCategory findCategoryByName = feedCategoryService.findCategoryByName(categoryName);
        assertNotNull(findCategoryByName);
    }

    @Test
    public void findCategoryByNameWithLike() throws Exception {
        List<String> categories = feedCategoryService.findCategoryNamesWithLike(categoryName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

    @Test
    public void findCategoryNamesInLowerCaseWithLike() throws Exception {
        List<String> categories = feedCategoryService.findCategoryNamesInLowerCaseWithLike(categoryName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

}