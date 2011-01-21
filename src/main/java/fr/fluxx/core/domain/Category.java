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

package fr.fluxx.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "NAME" }))
@NamedNativeQueries({ 
	@NamedNativeQuery(name = "findNumItemByCategory", query = "SELECT c.NAME, COUNT(ic.ITEM_ID) FROM CATEGORY c, item_category ic WHERE c.ID = ic.CATEGORIES_ID GROUP BY c.name"),
	@NamedNativeQuery(name = "findCategoriesByFeed", query = "select category.NAME from FEED feed, ITEM item, ITEM_CATEGORY item_category, CATEGORY category where feed.ID = ?1 and item.FEED_ID = feed.ID and item_category.ITEM_ID = item.ID and category.ID = item_category.CATEGORIES_ID order by category.NAME asc")
})
@NamedQueries({
		@NamedQuery(name = "findCategoryByName", query = "SELECT category FROM Category category WHERE category.name = :name"),
		@NamedQuery(name = "findCategoriesByName", query = "SELECT category FROM Category category WHERE LOWER(category.name) LIKE :name ORDER BY category.name"),
		@NamedQuery(name = "deleteAllCategories", query = "DELETE FROM Category category") })
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable=false)
	private String name;

	public Category() {
		
	}

	public Category(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Category) {
			Category c = (Category) obj;
			if (name == null || c.name == null) {
				return false;
			}
			return name.equals(c.name);
		}
 		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
