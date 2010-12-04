package fr.kaddath.apps.fluxx.service;

import org.junit.Assert;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndEntryImpl;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.InvalidItemException;

public class ItemBuilderServiceTest extends AbstractTest {

	private static final String TOO_LONG_LINK = "http://rss.feedsportal.com/c/629/f/502211/s/fe701e8/l/0Lrss0Bfeedsportal0N0Cc0C6290Cf0C50A22110Cs0Cfe70A1e80Cl0C0ALrss0ABfeedsportal0AN0ACc0AC6290ACf0AC50AA22110ACs0ACfe70AA1e80ACl0AC0AALrss0AABfeedsportal0AAN0AACc0AAC6290AACf0AAC50AAA22110AACs0AACfe70AAA1e80AACl0AAC0AAALrss0AAAB0AAAA1net0AAAN0AAACpro0AAAB0AAAA1net0AAAN0AAACeditorial0AAAC5238470AAACjuniper0AAAEmuscle0AAAEson0AAAEoffre0AAAEde0AAAEdiffusion0AAAEvideo0AAAC0AAADr0AAAF0AAACrss0AAACdossiersentreprise0AAABxml0AACstory0AAA10AABhtm0ACstory0AA10ABhtm0Cstory0A10Bhtm/story01.htm";

	@Test
	public void should_not_create_an_item_with_long_url() {
		Assert.assertTrue("you test link is too short", TOO_LONG_LINK.length() >= Item.MAX_ITEM_LINK_SIZE);

		SyndEntryImpl syndEntryImpl = new SyndEntryImpl();
		syndEntryImpl.setLink(TOO_LONG_LINK);
		try {
			itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
			Assert.fail("should fail because link is too long");
		} catch (InvalidItemException e) {
		}
	}
}
