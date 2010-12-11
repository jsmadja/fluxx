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

import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@StaticMetamodel(CustomFeed.class)
public class CustomFeed_ {

    public static volatile SingularAttribute<CustomFeed, String> id;
    public static volatile SingularAttribute<CustomFeed, String> name;
    public static volatile SingularAttribute<CustomFeed, Integer> numLastDay;
    public static volatile ListAttribute<CustomFeed, Feed> feeds;
    public static volatile SingularAttribute<CustomFeed, Date> referentDay;
    public static volatile SingularAttribute<CustomFeed, String> username;
    public static volatile SingularAttribute<CustomFeed, String> category;

}
