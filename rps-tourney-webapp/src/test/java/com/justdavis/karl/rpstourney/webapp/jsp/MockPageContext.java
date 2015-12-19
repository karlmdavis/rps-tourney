package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

import org.springframework.mock.web.MockServletContext;

/**
 * A mock {@link PageContext} for use in tests.
 */
@SuppressWarnings("deprecation")
final class MockPageContext extends PageContext {
	private final JspWriter jspWriter;
	private final ServletContext servletContext;
	private final Map<String, Object> attributes;

	/**
	 * Constructs a new {@link MockPageContext} instance.
	 * 
	 * @param jspWriter
	 *            the {@link JspWriter} to return for calls to {@link #getOut()}
	 */
	public MockPageContext(JspWriter jspWriter) {
		this(jspWriter, null);
	}

	/**
	 * Constructs a new {@link MockPageContext} instance.
	 * 
	 * @param jspWriter
	 *            the {@link JspWriter} to return for calls to {@link #getOut()}
	 * @param servletContext
	 *            the {@link ServletContext} to return for calls to
	 *            {@link #getServletContext()}
	 */
	public MockPageContext(JspWriter jspWriter, ServletContext servletContext) {
		this.jspWriter = jspWriter;
		this.servletContext = servletContext != null ? servletContext : new MockServletContext();
		this.attributes = new HashMap<>();
	}

	/**
	 * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
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
		return attributes.get(name);
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
		return new MockELContext();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#initialize(javax.servlet.Servlet,
	 *      javax.servlet.ServletRequest, javax.servlet.ServletResponse,
	 *      java.lang.String, boolean, int, boolean)
	 */
	@Override
	public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL,
			boolean needsSession, int bufferSize, boolean autoFlush)
					throws IOException, IllegalStateException, IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#release()
	 */
	@Override
	public void release() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getSession()
	 */
	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getPage()
	 */
	@Override
	public Object getPage() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getRequest()
	 */
	@Override
	public ServletRequest getRequest() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getResponse()
	 */
	@Override
	public ServletResponse getResponse() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getException()
	 */
	@Override
	public Exception getException() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getServletConfig()
	 */
	@Override
	public ServletConfig getServletConfig() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getServletContext()
	 */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#forward(java.lang.String)
	 */
	@Override
	public void forward(String relativeUrlPath) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#include(java.lang.String)
	 */
	@Override
	public void include(String relativeUrlPath) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#include(java.lang.String, boolean)
	 */
	@Override
	public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Exception)
	 */
	@Override
	public void handlePageException(Exception e) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Throwable)
	 */
	@Override
	public void handlePageException(Throwable t) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * A mock {@link ELContext} implementation for use with
	 * {@link MockPageContext}.
	 */
	private static final class MockELContext extends ELContext {
		/**
		 * @see javax.el.ELContext#getELResolver()
		 */
		@Override
		public ELResolver getELResolver() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.el.ELContext#getFunctionMapper()
		 */
		@Override
		public FunctionMapper getFunctionMapper() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.el.ELContext#getVariableMapper()
		 */
		@Override
		public VariableMapper getVariableMapper() {
			throw new UnsupportedOperationException();
		}
	}
}