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

package fr.kaddath.apps.fluxx;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import fr.kaddath.apps.fluxx.domain.Feed;

public class FeedUpdateData {

	private static final Logger LOG = Logger.getLogger(FeedUpdateData.class.getName());

	private static final int ONE_MEGABYTE = 1048576;

	private int analyzeSize = 0;
	private int downloadSize = 0;

	public void log() {
		logUpdateStats();
	}

	public void addDownload(Feed feed) {
		downloadSize += feed.getSize();
	}

	public void addAnalyze(Feed feed) {
		analyzeSize += feed.getSize();
	}

	private void logUpdateStats() {
		LOG.info(convertInMegaBytes(downloadSize) + " MB downloaded, " + convertInMegaBytes(analyzeSize)
				+ " MB analyzed");
	}

	private String convertInMegaBytes(int size) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		return df.format((double) size / ONE_MEGABYTE);
	}

}
