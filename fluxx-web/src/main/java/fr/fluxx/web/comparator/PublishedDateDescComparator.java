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

package fr.fluxx.web.comparator;

import java.io.Serializable;
import java.util.Comparator;

import fr.fluxx.core.domain.Item;

public class PublishedDateDescComparator implements Comparator<Item>, Serializable {

	@Override
	public int compare(Item o1, Item o2) {

		if (o1 == null || o2 == null) {
			return 0;
		}

		return o2.getPublishedDate().compareTo(o1.getPublishedDate());
	}
}
