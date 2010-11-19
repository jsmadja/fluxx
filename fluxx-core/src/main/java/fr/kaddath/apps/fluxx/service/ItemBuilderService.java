package fr.kaddath.apps.fluxx.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;

import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.FeedCategory;
import fr.kaddath.apps.fluxx.domain.Item;

@Stateless
public class ItemBuilderService {

	@PersistenceContext
	EntityManager em;

	@EJB
	DownloadableItemService downloadableItemService;

	@EJB
	FeedCategoryService feedCategoryService;

	public Item createItemFromSyndEntry(SyndEntryImpl syndEntryImpl) {
		Item item = new Item();
		addItemInformations(syndEntryImpl, item);
		em.persist(item);
		addFeedCategories(syndEntryImpl, item);
		addDownloadableItems(syndEntryImpl, item);
		return item;
	}

	private void addItemInformations(SyndEntryImpl syndEntryImpl, Item item) {
		item.setAuthor(syndEntryImpl.getAuthor());
		if (syndEntryImpl.getDescription() != null) {
			item.setDescription(syndEntryImpl.getDescription().getValue());
			item.setDescriptionType(syndEntryImpl.getDescription().getType());
		}
		item.setLink(syndEntryImpl.getLink());
		item.setPublishedDate(syndEntryImpl.getPublishedDate());
		item.setTitle(syndEntryImpl.getTitle());
		item.setUpdatedDate(syndEntryImpl.getUpdatedDate());
		item.setUri(syndEntryImpl.getUri());
	}

	@SuppressWarnings("unchecked")
	private void addFeedCategories(SyndEntryImpl syndEntryImpl, Item item) {
		Set<FeedCategory> feedCategories = new HashSet<FeedCategory>();
		List<SyndCategory> syndCategories = syndEntryImpl.getCategories();
		for (SyndCategory syndCategorie : syndCategories) {
			String categoryName = syndCategorie.getName();
			FeedCategory feedCategory = null;
			if (StringUtils.isNotBlank(categoryName)) {
				feedCategory = feedCategoryService.findCategoryByName(categoryName);
				if (feedCategory == null) {
					feedCategory = new FeedCategory();
					feedCategory.setName(categoryName);
					em.persist(feedCategory);
					em.flush();
				}
				feedCategories.add(feedCategory);
			}
		}
		item.setFeedCategories(feedCategories);
	}

	@SuppressWarnings("unchecked")
	private void addDownloadableItems(SyndEntryImpl syndEntryImpl, Item item) {
		Set<DownloadableItem> downloadableItems = new HashSet<DownloadableItem>();
		List<SyndEnclosure> syndEnclosures = syndEntryImpl.getEnclosures();
		for (SyndEnclosure syndEnclosure : syndEnclosures) {
			DownloadableItem downloadableItem = null;
			String url = syndEnclosure.getUrl();
			if (StringUtils.isNotBlank(url)) {
				downloadableItem = downloadableItemService.findByUrl(url);
			}
			if (downloadableItem == null) {
				downloadableItem = new DownloadableItem();
				downloadableItem.setUrl(url);
				downloadableItem.setFileLength(syndEnclosure.getLength());
				downloadableItem.setType(syndEnclosure.getType());
				downloadableItem.setItem(item);
				em.persist(downloadableItem);
				em.flush();
			}
			downloadableItems.add(downloadableItem);
		}
		item.setDownloadableItems(downloadableItems);
	}

}
