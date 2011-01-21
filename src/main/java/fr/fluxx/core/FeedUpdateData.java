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

package fr.fluxx.core;

import java.text.DecimalFormat;

import fr.fluxx.core.domain.Feed;

public class FeedUpdateData {

	private static final int MAXIMUM_FRACTION_DIGIT = 3;

	private static final int ONE_MEGABYTE = 1048576;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

	private int downloadSize = 0;
	private int numFeeds = 0;

	static {
		DECIMAL_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGIT);
	}

	public void add(Feed feed) {
		if (feed != null && feed.getSize() > 0) {
			downloadSize += feed.getSize();
			numFeeds++;
		}
	}

	public String getDownloadSizeInMBytes() {
		return DECIMAL_FORMAT.format((double) downloadSize / ONE_MEGABYTE);
	}

	public int getDownloadSizeInBytes() {
		return downloadSize;
	}

	public int getNumFeeds() {
		return numFeeds;
	}
}
