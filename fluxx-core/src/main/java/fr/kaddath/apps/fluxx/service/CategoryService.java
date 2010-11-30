package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@SuppressWarnings("unchecked")
@Interceptors({ ChronoInterceptor.class })
public class CategoryService {

	@PersistenceContext
	EntityManager em;

	public List<Pair<String, Integer>> findNumItemFeedsByCategory() {
		List<Pair<String, Integer>> numItemsByCategory = new ArrayList<Pair<String, Integer>>();
		Query q = em
				.createNativeQuery("SELECT fc.NAME, count(ifc.ITEM_ID) FROM CATEGORY fc, item_category ifc WHERE fc.name = ifc.CATEGORIES_NAME GROUP BY fc.name");
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
		Category category = new Category();
		category.setName(categoryName);
		em.persist(category);
		em.flush();
		return category;
	}
}
