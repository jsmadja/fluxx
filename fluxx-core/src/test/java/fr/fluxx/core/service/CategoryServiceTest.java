/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.fluxx.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.fluxx.core.AbstractTest;
import fr.fluxx.core.collection.Pair;
import fr.fluxx.core.collection.PairList;
import fr.fluxx.core.domain.Category;

public class CategoryServiceTest extends AbstractTest {

	@Test
	public void should_add_a_valid_category() {
		String name = createRandomString();
		Category category = categoryService.create(name);
		assertEquals(name, category.getName());
	}

	@Test
	public void should_return_the_number_of_item_by_category() throws Exception {
		createFeedWithCategories();
		PairList<String, Integer> allNumItemByCategory = categoryService.findNumItemByCategory();
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
	public void should_return_an_existing_category_case_insensitive() throws Exception {
		Category category = createCategory();
		String shortName = category.getName().substring(1);
		List<Category> categories = categoryService.findCategoriesByName(shortName, 1);
		assertNotNull(categories);
		assertTrue(categories.size() > 0);
	}

}