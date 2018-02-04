package com.justdavis.karl.rpstourney.service.api.auth;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import com.justdavis.karl.rpstourney.service.api.game.Game;

/**
 * <p>
 * Each {@link AuditAccountMerge} instance records an
 * {@link IAccountsResource#mergeAccount(long, java.util.UUID)} operation that
 * has been performed, and provides the information that would (theoretically)
 * be needed to undo it.
 * </p>
 * <p>
 * This class supports JPA. This class supports JAX-B.
 * </p>
 */
@Entity
@Table(name = "`AuditAccountMerges`")
public class AuditAccountMerge {
	/*
	 * FIXME Would rather use GenerationType.IDENTITY, but can't, due to
	 * https://hibernate.atlassian.net/browse/HHH-9430.
	 */
	/*
	 * FIXME Would rather sequence name was mixed-case, but it can't be, due to
	 * https://hibernate.atlassian.net/browse/HHH-9431.
	 */
	@Id
	@Column(name = "`id`", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuditAccountMerges_id_seq")
	@SequenceGenerator(name = "AuditAccountMerges_id_seq", sequenceName = "`auditaccountmerges_id_seq`")
	private long id;

	@Column(name = "`mergeTimestamp`", nullable = false, updatable = false)
	private Instant mergeTimestamp;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH })
	@JoinColumn(name = "`targetAccountId`")
	private Account targetAccount;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	/*
	 * FIXME The 'inverseJoinColumns' column here can't be quoted unless/until
	 * https://hibernate.atlassian.net/browse/HHH-9427 is resolved.
	 */
	@JoinTable(name = "`AuditAccountLoginMerges`", joinColumns = {
			@JoinColumn(name = "`auditAccountMergeId`") }, inverseJoinColumns = {
					@JoinColumn(name = "loginIdentityId") })
	private Set<AbstractLoginIdentity> mergedLogins;

	@OneToMany(mappedBy = "parentAuditAccountMerge", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH })
	private Set<AuditAccountGameMerge> mergedGames;

	/**
	 * Constructs a new {@link AuditAccountMerge} instance.
	 * 
	 * @param targetAccount
	 *            the value to use for {@link #getTargetAccount()}
	 * @param mergedLogins
	 *            the value to use for {@link #getMergedLogins()}
	 */
	public AuditAccountMerge(Account targetAccount, Set<AbstractLoginIdentity> mergedLogins) {
		this.id = -1;
		this.mergeTimestamp = Instant.now();
		this.targetAccount = targetAccount;
		this.mergedLogins = mergedLogins;
		this.mergedGames = new HashSet<>();
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	AuditAccountMerge() {
		this.id = -1;
	}

	/**
	 * @return <code>true</code> if this {@link AuditAccountMerge} has been
	 *         assigned an ID (which it should if it's been persisted),
	 *         <code>false</code> if it has not
	 */
	public boolean hasId() {
		return id > -1;
	}

	/**
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link AuditAccountMerge} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link AuditAccountMerge} instance
	 */
	public long getId() {
		if (!hasId())
			throw new IllegalStateException("Field value not yet available.");

		return id;
	}

	/**
	 * @return the date-time that this {@link AuditAccountMerge} was created
	 */
	public Instant getMergeTimestamp() {
		return mergeTimestamp;
	}

	/**
	 * @return the {@link Account} that was merged into as part of the
	 *         {@link IAccountsResource#mergeAccount(long, java.util.UUID)}
	 *         operation
	 */
	public Account getTargetAccount() {
		return targetAccount;
	}

	/**
	 * @param targetAccount
	 *            the new value to use for {@link #getTargetAccount()}
	 */
	public void setTargetAccount(Account targetAccount) {
		this.targetAccount = targetAccount;
	}

	/**
	 * @return the {@link AbstractLoginIdentity} instances that were associated
	 *         with the old/merged {@link Account}
	 */
	public Set<AbstractLoginIdentity> getMergedLogins() {
		return mergedLogins;
	}

	/**
	 * @return the {@link AuditAccountGameMerge} instances that represent the
	 *         {@link Game}s that were altered as part of the
	 *         {@link IAccountsResource#mergeAccount(long, java.util.UUID)}
	 *         operation
	 */
	public Set<AuditAccountGameMerge> getMergedGames() {
		return mergedGames;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		/*
		 * Uses the id field (which has a UNIQUE constraint in the DB) when
		 * present, otherwise falls back to the superclass' implementation
		 * (mostly for the benefit of unit tests).
		 */

		if (hasId()) {
			/*
			 * Generated by Eclipse's
			 * "Source > Generate hashCode() and equals()..." function.
			 */
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (id ^ (id >>> 32));
			return result;
		} else {
			return super.hashCode();
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		/*
		 * Uses the id field (which has a UNIQUE constraint in the DB) when
		 * present, otherwise falls back to instance equality (mostly for the
		 * benefit of unit tests).
		 */

		if (hasId()) {
			/*
			 * Generated by Eclipse's
			 * "Source > Generate hashCode() and equals()..." function.
			 */
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AuditAccountMerge other = (AuditAccountMerge) obj;
			if (id != other.id)
				return false;
			return true;
		} else {
			return this == obj;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuditAccountMerge [id=");
		builder.append(id);
		builder.append(", mergeTimestamp=");
		builder.append(mergeTimestamp);
		builder.append(", targetAccount=");
		builder.append(targetAccount);
		builder.append(", mergedLogins=");
		builder.append(mergedLogins);
		builder.append(", mergedGames=");
		builder.append(mergedGames);
		builder.append("]");
		return builder.toString();
	}
}
