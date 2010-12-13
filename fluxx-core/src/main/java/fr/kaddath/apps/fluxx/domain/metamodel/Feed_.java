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

package fr.kaddath.apps.fluxx.domain.metamodel;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

@StaticMetamodel(Feed.class)
public class Feed_ {

	public static volatile SingularAttribute<Feed, Long> id;
	public static volatile SingularAttribute<Feed, String> url;
	public static volatile SingularAttribute<Feed, Boolean> complete;
	public static volatile ListAttribute<Feed, Item> items;
	public static volatile ListAttribute<Feed, CustomFeed_> customFeeds;
	public static volatile SingularAttribute<Feed, String> author;
	public static volatile SingularAttribute<Feed, String> description;
	public static volatile SingularAttribute<Feed, String> encoding;
	public static volatile SingularAttribute<Feed, String> feedType;
	public static volatile SingularAttribute<Feed, Boolean> inError;
	public static volatile SingularAttribute<Feed, String> title;
	public static volatile SingularAttribute<Feed, Date> lastUpdate;
	public static volatile SingularAttribute<Feed, Date> publishedDate;
	public static volatile SingularAttribute<Feed, Date> nextUpdate;
	public static volatile SingularAttribute<Feed, Double> publicationRatio;

}
