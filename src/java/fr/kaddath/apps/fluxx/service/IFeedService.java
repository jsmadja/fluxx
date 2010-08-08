/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import java.util.List;

/**
 *
 * @author jsmadja
 */
public interface IFeedService {

    void delete(Feed feed);

    List<Feed> findAllFeeds();

    List<Feed> findAllFeedsInError();

    List<Feed> findAllFeedsNotInError();

    List<Feed> findAvailableFeedsByAggregatedFeed(AggregatedFeed aggregatedFeed);

    List<Feed> findAvailableFeedsByAggregatedFeedWithFilter(AggregatedFeed aggregatedFeed, String filter);

    List<String> findCategoriesByFeedId(String feedId);

    Feed findFeedById(Long id);

    Feed findFeedByUrl(String url);

    List<Feed> findFeedsByCategoryWithLike(String filter);

    List<Feed> findFeedsByInError(boolean inError, int firstResult);

    List<Feed> findFeedsByItemWithLike(String filter);

    List<Feed> findFeedsWithLike(String filter);

    Long getNumFeeds();

}
