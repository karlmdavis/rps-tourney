package com.justdavis.karl.rpstourney.service.app.jpa;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import com.justdavis.karl.rpstourney.service.api.hibernate.InternetAddressUserType;

/**
 * <p>
 * A Hibernate plugin that registers the user type mappings provided by this project with Hibernate.
 * </p>
 * <p>
 * It's "plugged in" via Java's SPI mechanism; see
 * <code>src/main/resources/META-INF/services/org.hibernate.integrator.spi.Integrator</code> .
 * </p>
 */
public class HibernateUserTypesIntegrator implements Integrator {
	/**
	 * @see org.hibernate.integrator.spi.Integrator#integrate(org.hibernate.boot.Metadata,
	 *      org.hibernate.engine.spi.SessionFactoryImplementor, org.hibernate.service.spi.SessionFactoryServiceRegistry)
	 */
	@Override
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		MetadataImplementor metadataImplementor;
		if (metadata instanceof MetadataImplementor) {
			metadataImplementor = (MetadataImplementor) metadata;
		} else {
			throw new IllegalArgumentException(
					"Metadata was not assignable to MetadataImplementor: " + metadata.getClass());
		}
		// Register the custom user type mapping(s) with Hibernate.
		InternetAddressUserType emailAddressUserType = new InternetAddressUserType();
		metadataImplementor.getTypeResolver().registerTypeOverride(emailAddressUserType,
				new String[] { emailAddressUserType.returnedClass().getName() });
	}

	/**
	 * @see org.hibernate.integrator.spi.Integrator#disintegrate(org.hibernate.engine.spi.SessionFactoryImplementor,
	 *      org.hibernate.service.spi.SessionFactoryServiceRegistry)
	 */
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		// Nothing to do here.
	}
}
