package fr.kaddath.apps.fluxx.controller;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.service.CustomFeedService;

@ManagedBean
@Named(value = "listCustomFeed")
@SessionScoped
public class ListCustomFeedBean implements Serializable {

	private static final String MANAGE_AGGREGATEDFEED = "manage-aggregatedfeed";

	private String category;
	private String username;

	private int numLastDay = 3;

	@Inject
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
