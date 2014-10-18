package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Instant;

/**
 * Unit tests for {@link TemporalFormatTag}.
 */
public class TemporalFormatTagTest {
	/**
	 * Tests normal usage of {@link TemporalFormatTag}.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void normalUsage() throws JspException, IOException {
		// Create the mock objects to use.
		MockJspWriter jspWriter = new MockJspWriter();
		MockJspContext jspContext = new MockJspContext(jspWriter);

		// Create the tag to test.
		TemporalFormatTag timeTag = new TemporalFormatTag();
		timeTag.setJspContext(jspContext);

		// Test the tag.
		Instant now = Instant.EPOCH;
		timeTag.setValue(now);
		timeTag.doTag();
		Assert.assertEquals("1970-01-01T00:00:00Z", jspWriter.output.toString());
	}
}
