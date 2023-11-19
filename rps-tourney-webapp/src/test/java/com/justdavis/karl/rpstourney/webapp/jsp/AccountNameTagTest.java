package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.context.SecurityContextImpl;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient;
import com.justdavis.karl.rpstourney.webapp.security.WebServiceAccountAuthentication;

/**
 * Unit tests for {@link AccountNameTag}.
 */
public class AccountNameTagTest {
	/**
	 * Tests usage of {@link AccountNameTag} when the user is not authenticated.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withUnauthenticatedUser() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(null);
		MessageSource messageSource = createMessageSource();
		MockAccountsClient accountsClient = new MockAccountsClient();
		MockJspWriter jspWriter = new MockJspWriter();
		ServletContext servletContext = new MockServletContext();
		MockPageContext pageContext = new MockPageContext(jspWriter, servletContext);

		// Create the tag to test.
		AccountNameTag accountNameTag = new AccountNameTag();
		accountNameTag.setMockSecurityContext(securityContext);
		accountNameTag.setMessageSource(messageSource);
		accountNameTag.setAccountsClient(accountsClient);
		accountNameTag.setPageContext(pageContext);

		// Test the tag.
		accountNameTag.doEndTag();
		Assert.assertEquals("foo", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link AccountNameTag} when the user is authenticated but has no name.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withAnonymousUser() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		final Account anonAccount = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(anonAccount));
		MessageSource messageSource = createMessageSource();
		MockAccountsClient accountsClient = new MockAccountsClient() {
			/**
			 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#getAccount()
			 */
			@Override
			public Account getAccount() {
				return anonAccount;
			}
		};
		MockJspWriter jspWriter = new MockJspWriter();
		ServletContext servletContext = new MockServletContext();
		MockPageContext pageContext = new MockPageContext(jspWriter, servletContext);

		// Create the tag to test.
		AccountNameTag accountNameTag = new AccountNameTag();
		accountNameTag.setMockSecurityContext(securityContext);
		accountNameTag.setMessageSource(messageSource);
		accountNameTag.setAccountsClient(accountsClient);
		accountNameTag.setPageContext(pageContext);

		// Test the tag.
		accountNameTag.doEndTag();
		Assert.assertEquals("foo", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link AccountNameTag} when the user is authenticated and has a name.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withNamedUser() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		final Account namedAccount = new Account();
		namedAccount.setName("bar");
		securityContext.setAuthentication(new WebServiceAccountAuthentication(namedAccount));
		MessageSource messageSource = createMessageSource();
		MockAccountsClient accountsClient = new MockAccountsClient() {
			/**
			 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#getAccount()
			 */
			@Override
			public Account getAccount() {
				return namedAccount;
			}
		};
		MockJspWriter jspWriter = new MockJspWriter();
		ServletContext servletContext = new MockServletContext();
		MockPageContext pageContext = new MockPageContext(jspWriter, servletContext);

		// Create the tag to test.
		AccountNameTag accountNameTag = new AccountNameTag();
		accountNameTag.setMockSecurityContext(securityContext);
		accountNameTag.setMessageSource(messageSource);
		accountNameTag.setAccountsClient(accountsClient);
		accountNameTag.setPageContext(pageContext);

		// Test the tag.
		accountNameTag.doEndTag();
		Assert.assertEquals("bar", jspWriter.output.toString());
	}

	/**
	 * @return a mock {@link MessageSource}
	 */
	private MessageSource createMessageSource() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("accountName.anon", Locale.getDefault(), "foo");
		return messageSource;
	}
}
