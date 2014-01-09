package com.justdavis.karl.rpstourney.webservice.jpa;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.hibernate.HibernateException;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.compare.EqualsHelper;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.hibernate.usertype.UserType;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * <p>
 * A Hibernate {@link UserType} for persisting {@link InternetAddress} fields.
 * Any JPA field that's an {@link InternetAddress} will have to add a Hibernate
 * {@link Type} annotation referencing this class, e.g.:
 * </p>
 * 
 * <pre>
 * &#64;Entity
 * public class SomeEntity {
 *   &#64;org.hibernate.annotations.Type(type = InternetAddressUserType.TYPE_NAME)
 *   private final InternetAddress emailAddress;
 *   ...
 * }
 * </pre>
 */
public class InternetAddressUserType implements UserType {
	/**
	 * The same as {@code InternetAddressUserType.class.getName()}, but a
	 * constant expression that can be referenced in annotations.
	 */
	public static final String TYPE_NAME = "com.justdavis.karl.rpstourney.webservice.jpa.InternetAddressUserType";

	/**
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		return new int[] { VarcharTypeDescriptor.INSTANCE.getSqlType() };
	}

	/**
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class<?> returnedClass() {
		return InternetAddress.class;
	}

	/**
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return EqualsHelper.equals(x, y);
	}

	/**
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/**
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
	 *      java.lang.String[], org.hibernate.engine.spi.SessionImplementor,
	 *      java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		if (names.length != 1)
			throw new BadCodeMonkeyException();

		String emailAddressValue = rs.getString(names[0]);
		if (emailAddressValue != null)
			try {
				return new InternetAddress(emailAddressValue, true);
			} catch (AddressException e) {
				throw new HibernateException(e);
			}
		else
			return null;
	}

	/**
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
	 *      java.lang.Object, int, org.hibernate.engine.spi.SessionImplementor)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {

		if (value != null) {
			if (!(value instanceof InternetAddress))
				throw new HibernateException("Unexpected value type: " + value);

			InternetAddress emailAddress = (InternetAddress) value;
			st.setString(index, emailAddress.toString());
		} else {
			st.setNull(index, VarcharTypeDescriptor.INSTANCE.getSqlType());
		}
	}

	/**
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;
		if (!(value instanceof InternetAddress))
			throw new HibernateException("Unexpected value type: " + value);

		InternetAddress emailAddress = (InternetAddress) value;
		return emailAddress.clone();
	}

	/**
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/**
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		if (value == null)
			return null;
		if (!(value instanceof InternetAddress))
			throw new HibernateException("Unexpected value type: " + value);

		InternetAddress emailAddress = (InternetAddress) value;
		return emailAddress.toString();
	}

	/**
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
	 *      java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		if (cached == null)
			return null;
		if (!(cached instanceof String))
			throw new HibernateException("Unexpected cached type: " + cached);

		String emailAddressValue = (String) cached;
		try {
			return new InternetAddress(emailAddressValue, true);
		} catch (AddressException e) {
			throw new HibernateException(e);
		}
	}

	/**
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return deepCopy(original);
	}

}
