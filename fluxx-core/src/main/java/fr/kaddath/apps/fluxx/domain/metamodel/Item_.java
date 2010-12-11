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

import fr.kaddath.apps.fluxx.domain.Category;
import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(Item.class)
public class Item_ {
    public static volatile SingularAttribute<Item, Feed> feed;
    public static volatile SingularAttribute<Item, Long> id;
    public static volatile SingularAttribute<Item, Date> publishedDate;
    public static volatile SingularAttribute<Item, Date> updatedDate;
    public static volatile SingularAttribute<Item, String> author;
    public static volatile SingularAttribute<Item, String> title;
    public static volatile SingularAttribute<Item, String> description;
    public static volatile SingularAttribute<Item, String> link;
    public static volatile SingularAttribute<Item, String> descriptionType;
    public static volatile SetAttribute<Item, Category> categories;
    public static volatile SetAttribute<Item, DownloadableItem> downloadableItems;
    public static volatile SingularAttribute<Item, String> uri;
}
