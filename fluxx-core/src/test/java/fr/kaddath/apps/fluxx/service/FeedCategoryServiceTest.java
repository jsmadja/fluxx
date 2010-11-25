package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Category;

public class FeedCategoryServiceTest extends AbstractTest {

    @Test
    public void add() {
        createCategory();
    }

    @Test
    public void findNumItemFeedsByCategory() throws Exception {
        createFeedWithDownloadableItems();
        List<String[]> findNumItemFeedsByCategory = categoryService.findNumItemFeedsByCategory();
        assertNotNull(findNumItemFeedsByCategory);
        assertTrue(findNumItemFeedsByCategory.size() > 0);
    }

    @Test
    public void findCategoryByName() throws Exception {
        Category category = createCategory();
        Category categoryFound = categoryService.findCategoryByName(category.getName());
        assertNotNull(categoryFound);
        assertEquals(category.getName(), categoryFound.getName());
    }

    @Test
    public void findCategoryByNameWithLike() throws Exception {
        Category category = createCategory();
        String shortName = category.getName().substring(1);
        List<String> categories = categoryService.findCategoryNamesWithLike(shortName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

    @Test
    public void findCategoryNamesInLowerCaseWithLike() throws Exception {   
        Category category = createCategory();
        String shortName = category.getName().substring(1);
        List<String> categories = categoryService.findCategoryNamesInLowerCaseWithLike(shortName, 1);
        assertNotNull(categories);
        assertTrue(categories.size()>0);
    }

}