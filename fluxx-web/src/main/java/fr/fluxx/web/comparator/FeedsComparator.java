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

import fr.fluxx.core.domain.Feed;

public class FeedsComparator implements Comparator<Feed>, Serializable {

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
