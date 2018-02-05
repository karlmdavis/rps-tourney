package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Test;

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
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		TemporalFormatTag timeTag = new TemporalFormatTag();
		timeTag.setJspContext(pageContext);

		// Test the tag.
		Instant now = Instant.EPOCH;
		timeTag.setValue(now);
		timeTag.doTag();
		Assert.assertEquals("1970-01-01T00:00:00Z", jspWriter.output.toString());
	}
}
