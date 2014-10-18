package com.justdavis.karl.rpstourney.webapp.jsp;

import java.util.Enumeration;

import javax.el.ELContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

/**
 * A mock {@link JspContext} for use in tests.
 */
@SuppressWarnings("deprecation")
final class MockJspContext extends JspContext {
	private final JspWriter jspWriter;

	/**
	 * Constructs a new {@link MockJspContext} instance.
	 * 
	 * @param jspWriter
	 *            the {@link JspWriter} to return for calls to {@link #getOut()}
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
	 * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String, int)
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