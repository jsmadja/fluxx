package fr.kaddath.apps.fluxx.comparator;

import fr.kaddath.apps.fluxx.domain.Feed;

import java.util.Comparator;

public class FeedsComparator implements Comparator<Feed> {

    @Override
    public int compare(Feed o1, Feed o2) {

        if (o1 == null || o2 == null) {
            return 0;
        }

        if (o1.getTitle() == null || o2.getTitle() == null) {
            return 0;
        }

        String title1 = o1.getTitle().toLowerCase();
        String title2 = o2.getTitle().toLowerCase();
        return title1.compareTo(title2);
    }
}
