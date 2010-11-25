package fr.kaddath.apps.fluxx.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;

import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.InvalidItemException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@Interceptors({ ChronoInterceptor.class })
public class ItemBuilderService {

	@PersistenceContext
	EntityManager em;

	@EJB
	DownloadableItemService downloadableItemService;

	@EJB
	CategoryService categoryService;

	public Item createItemFromSyndEntry(SyndEntryImpl syndEntryImpl) throws InvalidItemException {
		Item item = new Item();
		addItemInformations(syndEntryImpl, item);
		addFeedCategories(syndEntryImpl, item);
		addDownloadableItems(syndEntryImpl, item);
		validate(item);
		return item;
	}

	private void validate(Item item) throws InvalidItemException {
		String link = item.getLink();
		if (link.length() >= Item.MAX_ITEM_LINK_SIZE) {
			throw new InvalidItemException("Item contains an invalid item with link [" + link + "]");
		}
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
		Set<Category> categories = new HashSet<Category>();
		List<SyndCategory> syndCategories = syndEntryImpl.getCategories();
		for (SyndCategory syndCategorie : syndCategories) {
			String categoryName = syndCategorie.getName();
			Category category = null;
			if (StringUtils.isNotBlank(categoryName)) {
				category = categoryService.findCategoryByName(categoryName);
				if (category == null) {
					category = new Category();
					category.setName(categoryName);
					// obligatoire car les categories sont partages entre les feeds
					em.persist(category);
					em.flush();
				}
				categories.add(category);
			}
		}
		item.setCategories(categories);
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
				downloadableItem = new DownloadableItem(item);
				downloadableItem.setUrl(url);
				downloadableItem.setFileLength(syndEnclosure.getLength());
				downloadableItem.setType(syndEnclosure.getType());
			}
			downloadableItems.add(downloadableItem);
		}
		item.setDownloadableItems(downloadableItems);
	}

}
