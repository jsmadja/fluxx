package fr.kaddath.apps.fluxx.comparator;

import java.util.Comparator;

import fr.kaddath.apps.fluxx.domain.Item;

public class PublishedDateDescComparator implements Comparator<Item> {

	@Override
	public int compare(Item o1, Item o2) {

		if (o1 == null || o2 == null) {
			return 0;
		}
		
		return o2.getPublishedDate().compareTo(o1.getPublishedDate());
	}
}
