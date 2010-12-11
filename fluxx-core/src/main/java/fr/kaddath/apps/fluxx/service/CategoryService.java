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

package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@SuppressWarnings("unchecked")
@Interceptors({ ChronoInterceptor.class })
public class CategoryService {

	private static final String INVALID_CHARACTERS = "\"#/|!'";

	@PersistenceContext
	private EntityManager em;

	public List<Pair<String, Integer>> findNumItemFeedsByCategory() {
		List<Pair<String, Integer>> numItemsByCategory = new ArrayList<Pair<String, Integer>>();
		Query q = em
				.createNativeQuery("SELECT c.NAME, count(ic.ITEM_ID) FROM CATEGORY c, item_category ic WHERE c.ID = ic.CATEGORIES_ID GROUP BY c.name");
		for (Object o : q.getResultList()) {
			Object[] couple = (Object[]) o;
			numItemsByCategory.add(new Pair<String, Integer>(couple[0].toString(), (Integer) couple[1]));
		}
		return numItemsByCategory;
	}

	public Category findCategoryByName(String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("The name argument is required");
		}
		Query q = em.createQuery("SELECT category FROM Category category WHERE category.name = :name");
		q.setParameter("name", name);
		q.setMaxResults(1);
		List<Category> list = q.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> findCategoryNamesInLowerCaseWithLike(String name, Integer numCategories) {
		Query q = em.createNativeQuery("SELECT distinct(lower(name)) FROM CATEGORY WHERE lower(name) like '%"
				+ name.toLowerCase() + "%' order by lower(name)");
		q.setMaxResults(numCategories);
		return q.getResultList();
	}

	public List<String> findCategoryNamesWithLike(String name, Integer numCategories) {
		Query q = em.createNativeQuery("SELECT name FROM CATEGORY WHERE lower(name) like '%" + name.toLowerCase()
				+ "%' order by lower(name)");
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

	public void deleteAllCategories() {
		Query query = em.createQuery("select c from Category c");
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
