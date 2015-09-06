package com.justdavis.karl.rpstourney.service.api.auth.game;

import javax.mail.internet.InternetAddress;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.justdavis.karl.rpstourney.service.api.auth.AbstractLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.LoginProvider;
import com.justdavis.karl.rpstourney.service.api.hibernate.InternetAddressUserType;

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GAME}
 * logins.
 * </p>
 * <p>
 * Each {@link GameLoginIdentity} instance is basically just a username (always
 * an email address) and password hash, with an associated {@link Account}. When
 * creating one of these accounts, the game UI should also prompt users to flesh
 * out some of the {@link Account} details, e.g. their name, though that's not
 * required.
 * </p>
 * <p>
 * This class supports JPA.
 * </p>
 */
@Entity
@Table(name = "`GameLoginIdentities`")
@PrimaryKeyJoinColumn(name = "`id`", referencedColumnName = "`id`")
@XmlRootElement
public class GameLoginIdentity extends AbstractLoginIdentity implements
		ILoginIdentity {
	private static final long serialVersionUID = 5592372522747907472L;

	@org.hibernate.annotations.Type(type = InternetAddressUserType.TYPE_NAME)
	@Column(name = "`emailAddress`", unique = true, nullable = false)
	@XmlElement
	private InternetAddress emailAddress;

	/*
	 * This field is marked {@link XmlTransient} to help ensure it's never sent
	 * off of the server by mistake. Any web services wishing to use it in a
	 * response will have to do so explicitly.
	 */
	@Column(name = "`passwordHash`", nullable = false)
	@XmlTransient
	private String passwordHash;

	/**
	 * Constructs a new {@link GameLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param emailAddress
	 *            the value to use for {@link #getEmailAddress()}
	 * @param passwordHash
	 *            the value to use for {@link #getPasswordHash()}
	 */
	public GameLoginIdentity(Account account, InternetAddress emailAddress,
			String passwordHash) {
		super(account);

		this.emailAddress = emailAddress;
		this.passwordHash = passwordHash;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	GameLoginIdentity() {
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getLoginProvider()
	 */
	@Override
	public LoginProvider getLoginProvider() {
		return LoginProvider.GAME;
	}

	/**
	 * @return the email address that the user's account is tied to
	 */
	public InternetAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * <p>
	 * Returns the scrypt hash of the user's password that is stored for
	 * authentication purposes.
	 * </p>
	 * <p>
	 * In general, this should never be sent off of the server.
	 * </p>
	 * 
	 * @return the scrypt hash of the user's password
	 * @see GameAuthService#hashPassword(String)
	 * @see GameAuthService#checkPassword(String, GameLoginIdentity)
	 */
	public String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameLoginIdentity [id=");
		builder.append(id);
		/*
		 * Can't just include account.toString(), as that would create a
		 * recursive never-ending loop.
		 */
		builder.append(", account.getId()=");
		builder.append(account.hasId() ? account.getId() : "N/A");
		builder.append(", createdTimestamp=");
		builder.append(createdTimestamp);
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		/*
		 * Can't print out the passwordHash itself, as that value needs to be
		 * very carefully protected.
		 */
		builder.append(", passwordHash=");
		builder.append(passwordHash != null ? "(not null)" : "(null)");
		builder.append("]");
		return builder.toString();
	}

}
