package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Enumeration;

import javax.el.ELContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Instant;

/**
 * Unit tests for {@link TemporalFormatTag}.
 */
@SuppressWarnings("deprecation")
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
		Assert.assertEquals("1970-01-01T00:00Z", jspWriter.output.toString());
	}

	/**
	 * A mock {@link JspContext} for use in tests.
	 */
	private static final class MockJspContext extends JspContext {
		private final JspWriter jspWriter;

		/**
		 * Constructs a new {@link MockJspContext} instance.
		 * 
		 * @param jspWriter
		 *            the {@link JspWriter} to return for calls to
		 *            {@link #getOut()}
		 */
		public MockJspContext(JspWriter jspWriter) {
			this.jspWriter = jspWriter;
		}

		/**
		 * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public void setAttribute(String name, Object value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String,
		 *      java.lang.Object, int)
		 */
		@Override
		public void setAttribute(String name, Object value, int scope) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String)
		 */
		@Override
		public Object getAttribute(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String, int)
		 */
		@Override
		public Object getAttribute(String name, int scope) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#findAttribute(java.lang.String)
		 */
		@Override
		public Object findAttribute(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String)
		 */
		@Override
		public void removeAttribute(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String,
		 *      int)
		 */
		@Override
		public void removeAttribute(String name, int scope) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getAttributesScope(java.lang.String)
		 */
		@Override
		public int getAttributesScope(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getAttributeNamesInScope(int)
		 */
		@Override
		public Enumeration<String> getAttributeNamesInScope(int scope) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getOut()
		 */
		@Override
		public JspWriter getOut() {
			return jspWriter;
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getExpressionEvaluator()
		 */
		@Override
		public ExpressionEvaluator getExpressionEvaluator() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getVariableResolver()
		 */
		@Override
		public VariableResolver getVariableResolver() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspContext#getELContext()
		 */
		@Override
		public ELContext getELContext() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * A mock {@link JspWriter} implementation for use in tests.
	 */
	private static final class MockJspWriter extends JspWriter {
		private final StringBuilder output;

		/**
		 * Constructs a new {@link MockJspWriter} instance.
		 */
		public MockJspWriter() {
			super(1, false);

			this.output = new StringBuilder();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#newLine()
		 */
		@Override
		public void newLine() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(boolean)
		 */
		@Override
		public void print(boolean b) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(char)
		 */
		@Override
		public void print(char c) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(int)
		 */
		@Override
		public void print(int i) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(long)
		 */
		@Override
		public void print(long l) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(float)
		 */
		@Override
		public void print(float f) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(double)
		 */
		@Override
		public void print(double d) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(char[])
		 */
		@Override
		public void print(char[] s) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
		 */
		@Override
		public void print(String s) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
		 */
		@Override
		public void print(Object obj) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println()
		 */
		@Override
		public void println() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(boolean)
		 */
		@Override
		public void println(boolean x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(char)
		 */
		@Override
		public void println(char x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(int)
		 */
		@Override
		public void println(int x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(long)
		 */
		@Override
		public void println(long x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(float)
		 */
		@Override
		public void println(float x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(double)
		 */
		@Override
		public void println(double x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(char[])
		 */
		@Override
		public void println(char[] x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
		 */
		@Override
		public void println(String x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
		 */
		@Override
		public void println(Object x) throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#clear()
		 */
		@Override
		public void clear() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#clearBuffer()
		 */
		@Override
		public void clearBuffer() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#flush()
		 */
		@Override
		public void flush() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#close()
		 */
		@Override
		public void close() throws IOException {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.servlet.jsp.JspWriter#getRemaining()
		 */
		@Override
		public int getRemaining() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see java.io.Writer#write(char[], int, int)
		 */
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			output.append(cbuf, off, len);
		}
	}
}
