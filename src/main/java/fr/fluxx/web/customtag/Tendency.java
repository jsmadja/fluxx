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

package fr.fluxx.web.customtag;

import java.io.IOException;
import java.util.Date;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import fr.fluxx.core.Services;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.resource.FluxxMessage;
import fr.fluxx.core.service.ItemService;

@FacesComponent(value = "tendency")
public class Tendency extends UIComponentBase {

	public static final int DAYS_IN_A_WEEK = 7;
	public static final int DAYS_IN_A_MONTH = 30;
	public static final int DAYS_IN_A_YEAR = 365;
	public static final int MILLISEC_IN_A_DAY = 1000 * 60 * 60 * 24;

	private final ItemService itemService;

	public Tendency() {
		super();
		itemService = Services.getItemService();
	}

	@Override
	public String getFamily() {
		return "fluxx";
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter responseWriter = context.getResponseWriter();
		Feed feed = (Feed) getAttributes().get("feed");

		float numItems = itemService.getNumItemsOfFeed(feed);

		int periodInDays = computePeriod(feed);
		if (periodInDays == 0) {
			periodInDays = Integer.MAX_VALUE;
		}

		String tendency = "";
		String img = "";

		// per day
		double itemsPerDay = round(numItems / periodInDays, 1);
		double itemsPerWeek = round(numItems * DAYS_IN_A_WEEK / periodInDays, 1);
		double itemsPerMonth = round((numItems * DAYS_IN_A_MONTH) / periodInDays, 1);
		double itemsPerYear = round((numItems * DAYS_IN_A_YEAR) / periodInDays, 1);
		if (itemsPerDay >= 1) {
			tendency = FluxxMessage.m("publication_per_day", itemsPerDay);
			img = "2-green.png";
		} else if (itemsPerWeek >= 1) {
			tendency = FluxxMessage.m("publication_per_week", itemsPerWeek);
			img = "1-green.png";
		} else if (itemsPerMonth >= 1) {
			tendency = FluxxMessage.m("publication_per_month", itemsPerMonth);
			img = "2-black.png";
		} else if (itemsPerYear >= 1) {
			tendency = FluxxMessage.m("publication_per_year", itemsPerMonth);
			img = "1-red.png";
		} else {
			img = "" + itemsPerDay;
		}

		responseWriter.startElement("img", this);
		responseWriter.writeAttribute("src", context.getExternalContext().getRequestContextPath()
				+ "/myimages/tendency/" + img, "src");
		responseWriter.writeAttribute("title", tendency, "src");
		responseWriter.endElement("img");
		responseWriter.startElement("span", this);
		responseWriter.write(tendency);
		responseWriter.endElement("span");
	}

	public double round(double what, int howmuch) {
		return ((int) (what * Math.pow(10, howmuch) + .5)) / Math.pow(10, howmuch);
	}

	private int computePeriod(Feed feed) {

		Item lastItem = itemService.findFirstItemOfFeed(feed);
		if (lastItem == null) {
			return 0;
		}
		long t = System.currentTimeMillis();
		Date date = lastItem.getPublishedDate();
		if (date == null) {
			return 0;
		}
		long time = date.getTime();
		return (int) (((float) (t - time)) / MILLISEC_IN_A_DAY);
	}
}
