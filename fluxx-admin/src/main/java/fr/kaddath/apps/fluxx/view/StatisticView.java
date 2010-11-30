package fr.kaddath.apps.fluxx.view;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateUtils;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.service.CustomFeedService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;

@Named(value = "statisticView")
@ApplicationScoped
public class StatisticView {

	@Inject
	FeedService feedService;

	@Inject
	ItemService itemService;

	@Inject
	CustomFeedService customFeedService;

	public long getNumFeeds() {
		return feedService.getNumFeeds();
	}

	public long getNumItems() {
		return itemService.getNumItems();
	}

	public long getNumAggregatedFeeds() {
		return customFeedService.getNumCustomFeeds();
	}

	public String getNumItemsByDayOfLastMonth() {
		return getNumItemsBetween(firstDayOfLastMonth(), firstDayOfCurrentMonth());
	}

	public String getNumItemsByDayOfCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return getNumItemsBetween(firstDayOfCurrentMonth(), cal);
	}

	public long getMaxNumItemsOfLastMonth() {
		return getMaxNumItemsBetween(firstDayOfLastMonth(), firstDayOfCurrentMonth());
	}

	public long getMaxNumItemsOfCurrentMonth() {
		Calendar cal = getInstance();
		cal.add(DAY_OF_MONTH, 1);
		return getMaxNumItemsBetween(firstDayOfCurrentMonth(), cal);
	}

	public Date getLastUpdate() {
		Feed feed = feedService.findLastUpdatedFeed();
		if (feed != null) {
			return feed.getLastUpdate();
		}
		return Calendar.getInstance().getTime();
	}

	/**
	 * [1, 1.2, 1.7, 1.5, .7, .3]
	 * 
	 * @return
	 */
	private String getNumItemsBetween(Calendar from, Calendar to) {
		StringBuilder sb = new StringBuilder("[");
		do {
			long numItemsByDay = itemService.getNumItemsByDay(from.getTime());
			sb.append(numItemsByDay).append(",");
			from.add(DAY_OF_MONTH, 1);
		} while (from.before(to));
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}

	// --
	public String getNumItemsByHourOfCurrentDay() {
		Calendar cal = getThisMorning();
		return getNumItemsByHourSince(cal);
	}

	public long getMaxNumItemsOfCurrentDay() {
		Calendar now = getThisMorning();
		Date date = new GregorianCalendar(now.get(YEAR), now.get(MONTH), now.get(DAY_OF_MONTH)).getTime();
		long max = 0;
		for (int i = 0; i < 24; i++) {
			long numItemsByHour = itemService.getNumItemsByHour(date);
			max = Math.max(max, numItemsByHour);
			date = DateUtils.addHours(date, 1);
		}
		return max;
	}

	private String getNumItemsByHourSince(Calendar now) {
		StringBuilder sb = new StringBuilder("[");
		Date beginDate = new GregorianCalendar(now.get(YEAR), now.get(MONTH), now.get(DAY_OF_MONTH)).getTime();

		for (int i = 0; i < 24; i++) {
			long numItemsByHour = itemService.getNumItemsByHour(beginDate);
			beginDate = DateUtils.addHours(beginDate, 1);
			sb.append(numItemsByHour).append(",");
		}
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}

	public Map<String, Float> getFeedTypesRepartition() {

		float sum = 0;
		Map<String, Long> map = feedService.getNumFeedType();
		Collection<Long> numFeeds = map.values();
		for (Long i : numFeeds) {
			sum += i;
		}

		Map<String, Float> percentages = new HashMap<String, Float>();

		for (String key : map.keySet()) {
			Float value = (map.get(key) * 100) / sum;
			System.out.println(key + " : " + value);
			percentages.put(key, value);
		}

		return percentages;
	}

	private Calendar getThisMorning() {
		Calendar now = Calendar.getInstance();
		Calendar cal = new GregorianCalendar(now.get(YEAR), now.get(MONTH), now.get(DAY_OF_MONTH));
		return cal;
	}

	// --

	private long getMaxNumItemsBetween(Calendar from, Calendar to) {
		long max = 0;
		do {
			long numItemsByDay = itemService.getNumItemsByDay(from.getTime());
			max = Math.max(max, numItemsByDay);
			from.add(DAY_OF_YEAR, 1);
		} while (from.before(to));
		return max;
	}

	private Calendar firstDayOfLastMonth() {
		Calendar date = Calendar.getInstance();
		date.add(MONTH, -1);
		date.set(DAY_OF_MONTH, 1);
		return date;
	}

	private Calendar firstDayOfCurrentMonth() {
		Calendar date = Calendar.getInstance();
		date.set(DAY_OF_MONTH, 1);
		return date;
	}
}