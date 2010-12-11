package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Category;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FeedCategoryServiceTest extends AbstractTest {

    @Test
    public void should_add_a_valid_category() {
        String name = createRandomString();
        Category category = categoryService.create(name);
        assertEquals(name, category.getName());
    }

    @Test
    public void should_return_the_number_of_item_by_category() throws Exception {
        createFeedWithCategories();
        List<Pair<String, Integer>> allNumItemByCategory = categoryService.findNumItemFeedsByCategory();
        assertNotNull(allNumItemByCategory);
        assertFalse(allNumItemByCategory.isEmpty());
        for (Pair<String, Integer> numItemByCategory : allNumItemByCategory) {
            assertTrue(numItemByCategory.left().length() > 0);
            assertTrue(numItemByCategory.right() > 0);
        }
    }

    @Test
    public void should_return_an_existing_category() throws Exception {
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
        assertTrue(categories.size() > 0);
    }

    @Test
    public void findCategoryNamesInLowerCaseWithLike() throws Exception {
        Category category = createCategory();
        String shortName = category.getName().substring(1);
        List<String> categories = categoryService.findCategoryNamesInLowerCaseWithLike(shortName, 1);
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }

}