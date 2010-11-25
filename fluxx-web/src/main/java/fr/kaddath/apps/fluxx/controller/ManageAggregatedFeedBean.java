package fr.kaddath.apps.fluxx.controller;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.service.CustomFeedService;

@ManagedBean
@Named(value = "manageAggregatedFeed")
@SessionScoped
public class ManageAggregatedFeedBean implements Serializable {

	private static final String MANAGE_AGGREGATEDFEED = "manage-aggregatedfeed";

	private String theme;
	private String username;

	private int numLastDay = 3;

	@Inject
	private CustomFeedService aggregatedFeedService;

	public String add() {
		aggregatedFeedService.addCustomFeed(username, theme, numLastDay);
		return MANAGE_AGGREGATEDFEED;
	}

	public String getName() {
		return theme;
	}

	public void setName(String name) {
		this.theme = name;
	}

	public int getNumLastDay() {
		return numLastDay;
	}

	public void setNumLastDay(int numLastDay) {
		this.numLastDay = numLastDay;
	}

}
