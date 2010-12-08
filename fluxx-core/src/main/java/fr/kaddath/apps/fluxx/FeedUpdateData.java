package fr.kaddath.apps.fluxx;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import fr.kaddath.apps.fluxx.domain.Feed;

public class FeedUpdateData {

	private static final Logger LOG = Logger.getLogger(FeedUpdateData.class.getName());

	private static final int ONE_MEGABYTE = 1048576;

	private static int analyzeSize = 0;
	private static int downloadSize = 0;

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
