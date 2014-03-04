package com.justdavis.karl.rpstourney.webservice.jpa;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import com.justdavis.karl.rpstourney.service.api.hibernate.InternetAddressUserType;

/**
 * <p>
 * A Hibernate plugin that registers the user type mappings provided by this
 * project with Hibernate.
 * </p>
 * <p>
 * It's "plugged in" via Java's SPI mechanism; see
 * <code>src/main/resources/META-INF/services/org.hibernate.integrator.spi.Integrator</code>
 * .
 * </p>
 */
public class HibernateUserTypesIntegrator implements Integrator {
	/**
	 * @see org.hibernate.integrator.spi.Integrator#integrate(org.hibernate.cfg.Configuration,
	 *      org.hibernate.engine.spi.SessionFactoryImplementor,
	 *      org.hibernate.service.spi.SessionFactoryServiceRegistry)
	 */
	@Override
	public void integrate(Configuration configuration,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		// Register the custom user type mapping(s) with Hibernate.
		InternetAddressUserType emailAddressUserType = new InternetAddressUserType();
		configuration
				.registerTypeOverride(emailAddressUserType,
						new String[] { emailAddressUserType.returnedClass()
								.getName() });
	}

	/**
	 * @see org.hibernate.integrator.spi.Integrator#integrate(org.hibernate.metamodel.source.MetadataImplementor,
	 *      org.hibernate.engine.spi.SessionFactoryImplementor,
	 *      org.hibernate.service.spi.SessionFactoryServiceRegistry)
	 */
	@Override
	public void integrate(MetadataImplementor metadata,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		// Nothing to do here.
	}

	/**
	 * @see org.hibernate.integrator.spi.Integrator#disintegrate(org.hibernate.engine.spi.SessionFactoryImplementor,
	 *      org.hibernate.service.spi.SessionFactoryServiceRegistry)
	 */
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		// Nothing to do here.
	}
}
