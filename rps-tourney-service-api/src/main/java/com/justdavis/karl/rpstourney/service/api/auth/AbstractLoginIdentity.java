package com.justdavis.karl.rpstourney.service.api.auth;

import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * <p>
 * A common base class for all {@link ILoginIdentity} implementations.
 * </p>
 * <p>
 * This is required by JPA, to enable queries of the
 * "select all logins for user 'foo'" variety. Specifically, JPA only supports
 * those queries for for supertypes if the types all share a common base
 * class/table.
 * </p>
 * <p>
 * This class is marked as {@link Serializable}, as Spring Security will store
 * instances of it as part of the authenticated {@link Principal}s in user
 * sessions (via {@link Account#getLogins()}).
 * </p>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "`LoginIdentities`")
@XmlType
@XmlSeeAlso({ GuestLoginIdentity.class, GameLoginIdentity.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractLoginIdentity implements ILoginIdentity, Serializable {
	private static final long serialVersionUID = 4133421893609326130L;

	/*
	 * FIXME Would rather sequence name was mixed-case, but it can't be, due to
	 * https://hibernate.atlassian.net/browse/HHH-9431.
	 */
	@Id
	@Column(name = "`id`", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LoginIdentities_id_seq")
	@SequenceGenerator(name = "LoginIdentities_id_seq", sequenceName = "`loginidentities_id_seq`")
	@XmlElement
	protected long id;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH })
	@JoinColumn(name = "`accountId`")
	@XmlTransient
	@JsonBackReference
	protected Account account;

	@Column(name = "`createdTimestamp`", nullable = false, updatable = false)
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	protected Instant createdTimestamp;

	/**
	 * Constructs a new {@link AbstractLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param createdTimestamp
	 *            the value to use for {@link #getCreatedTimestamp()}
	 */
	protected AbstractLoginIdentity(Account account, Instant createdTimestamp) {
		if (account == null)
			throw new IllegalArgumentException();
		if (createdTimestamp == null)
			throw new IllegalArgumentException();

		this.id = -1;
		this.account = account;
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * Constructs a new {@link AbstractLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 */
	protected AbstractLoginIdentity(Account account) {
		this(account, Instant.now());
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	protected AbstractLoginIdentity() {
		this.id = -1;
	}

	/**
	 * @return <code>true</code> if this {@link AbstractLoginIdentity} has been
	 *         assigned an ID (which it should if it's been persisted),
	 *         <code>false</code> if it has not
	 */
	public boolean hasId() {
		return id > -1;
	}

	/**
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link AbstractLoginIdentity} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link AbstractLoginIdentity} instance
	 */
	public long getId() {
		return id;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getAccount()
	 */
	@Override
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the new value to use for {@link #getAccount()}
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity#getCreatedTimestamp()
	 */
	@Override
	public Instant getCreatedTimestamp() {
		return createdTimestamp;
	}

	/**
	 * This method will be called by JAXB during unmarshalling, and allows
	 * instances of this class to rebuild their {@link #getAccount()} references
	 * (which would otherwise be lost due to the {@link XmlTransient} annotation
	 * on the field). This setup is necessary to avoid cycle problems, per
	 * <a href=
	 * "https://jaxb.java.net/guide/Mapping_cyclic_references_to_XML.html">
	 * Mapping cyclic references to XML</a>.
	 * 
	 * @param u
	 *            the JAXB {@link Unmarshaller} being used
	 * @param parent
	 *            the child element/object's containing/parent object (always an
	 *            Account, in this case)
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.account = (Account) parent;
	}
}
