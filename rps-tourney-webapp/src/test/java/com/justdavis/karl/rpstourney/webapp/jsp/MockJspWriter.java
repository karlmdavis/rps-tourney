package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * A mock {@link JspWriter} implementation for use in tests.
 */
final class MockJspWriter extends JspWriter {
	final StringBuilder output;

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
