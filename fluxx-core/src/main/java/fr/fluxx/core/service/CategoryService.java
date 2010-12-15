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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import fr.fluxx.core.collection.PairList;
import fr.fluxx.core.domain.Category;
import fr.fluxx.core.domain.Feed;

@Stateless
public class CategoryService {

	private static final String INVALID_CHARACTERS = "\"#/|!'";

	private static final int MAX_CATEGORIES_TO_RETRIEVE = 5;
	
	@PersistenceContext
	private EntityManager em;

	public PairList<String, Integer> findNumItemByCategory() {
		PairList<String, Integer> numItemsByCategory = new PairList<String, Integer>();
		Query q = em.createNamedQuery("findNumItemByCategory");
		for (Object o : q.getResultList()) {
			Object[] couple = (Object[]) o;
			numItemsByCategory.add(couple[0].toString(), (Integer) couple[1]);
		}
		return numItemsByCategory;
	}

	public Category findCategoryByName(String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("The name argument is required");
		}
		TypedQuery<Category> q = em.createNamedQuery("findCategoryByName", Category.class);
		q.setParameter("name", name);
		q.setMaxResults(1);
		List<Category> list = q.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<Category> findCategoriesByName(String name, Integer numCategories) {
		TypedQuery<Category> q = em.createNamedQuery("findCategoriesByName", Category.class);
		q.setParameter("name", '%' + name.toLowerCase() + '%');
		q.setMaxResults(numCategories);
		return q.getResultList();
	}

	public Category create(String categoryName) {
		if (categoryName != null) {
			categoryName = categoryName.trim();
			for (Character c : INVALID_CHARACTERS.toCharArray()) {
				categoryName = StringUtils.remove(categoryName, c);
			}
		}
		Category category = findCategoryByName(categoryName);
		if (category == null) {
			category = new Category(categoryName);
			em.persist(category);
			em.flush();
		}
		return category;
	}
	
	public List<String> findCategoriesByFeed(Feed feed) {
		TypedQuery<String> query = em.createNamedQuery("findCategoriesByFeed", String.class);
		query.setParameter(1, feed.getId());
		query.setMaxResults(MAX_CATEGORIES_TO_RETRIEVE);
		return query.getResultList();
	}

	public void deleteAllCategories() {
		TypedQuery<Category> query = em.createNamedQuery("findAllCategories", Category.class);
		List<Category> categories = query.getResultList();
		for (Category category : categories) {
			delete(category);
		}
	}

	private void delete(Category category) {
		Category categoryToRemove = em.find(Category.class, category.getId());
		em.remove(categoryToRemove);
		em.flush();
	}

}
