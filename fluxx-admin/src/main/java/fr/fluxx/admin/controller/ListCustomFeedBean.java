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

package fr.fluxx.admin.controller;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import fr.fluxx.core.service.CustomFeedService;

@ManagedBean
@Named(value = "listCustomFeed")
@SessionScoped
public class ListCustomFeedBean implements Serializable {

	private static final long serialVersionUID = 1949660677636196611L;

	private static final String MANAGE_AGGREGATEDFEED = "manage-aggregatedfeed";

	private String category;
	private String username;

	private int numLastDay = 3;

	@EJB
	private CustomFeedService customFeedService;

	public String add() {
		customFeedService.addCustomFeed(username, category, numLastDay);
		return MANAGE_AGGREGATEDFEED;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getNumLastDay() {
		return numLastDay;
	}

	public void setNumLastDay(int numLastDay) {
		this.numLastDay = numLastDay;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
