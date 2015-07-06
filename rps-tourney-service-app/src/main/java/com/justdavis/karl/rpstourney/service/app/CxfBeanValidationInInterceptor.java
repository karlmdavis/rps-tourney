package com.justdavis.karl.rpstourney.service.app;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.jaxrs.validation.ValidationUtils;
import org.apache.cxf.message.Message;

/**
 * <p>
 * This is a replacement for CXF's builtin
 * {@link JAXRSBeanValidationInInterceptor}. This customization supports
 * validation of messages handled by non-singleton JAX-RS resource beans. This
 * is needed as many of the beans in this project are request-scoped.
 * </p>
 * <p>
 * <strong>Warning:</strong> I'm not sure that this is entirely safe. I've
 * posted the following Stack Overflow question asking about that: <a href=
 * "http://stackoverflow.com/questions/31235977/is-it-safe-to-override-apache-cxfs-jaxrsbeanvalidationininterceptor-to-support"
 * >Is it safe to override Apache CXF's JAXRSBeanValidationInInterceptor to
 * support request-scoped resources?</a>.
 * </p>
 */
public class CxfBeanValidationInInterceptor extends
		JAXRSBeanValidationInInterceptor {
	/**
	 * This is a customization of the code in CXF's builtin
	 * {@link ValidationUtils#getResourceInstance(Message)}.
	 * 
	 * @see org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor#getServiceObject(org.apache.cxf.message.Message)
	 */
	@Override
	protected Object getServiceObject(Message message) {
		final OperationResourceInfo ori = message.getExchange().get(
				OperationResourceInfo.class);
		if (ori == null) {
			return null;
		}
		if (!ori.getClassResourceInfo().isRoot()) {
			return message.getExchange().get(
					"org.apache.cxf.service.object.last");
		}
		final ResourceProvider resourceProvider = ori.getClassResourceInfo()
				.getResourceProvider();

		return resourceProvider.getInstance(message);
	}
}
