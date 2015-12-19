package com.justdavis.karl.rpstourney.service.app.auth.guest;

import java.util.ArrayList;
import java.util.List;

import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.auth.MockAccountsDao;

/**
 * A mock {@link IGuestLoginIndentitiesDao} implementation for use in tests.
 * Stores {@link GuestLoginIdentity} instances in-memory.
 */
public final class MockGuestLoginIdentitiesDao implements IGuestLoginIndentitiesDao {
	public final List<GuestLoginIdentity> logins = new ArrayList<>();
	private final MockAccountsDao accountsDao;

	/**
	 * Constructs a new {@link MockGuestLoginIdentitiesDao} instance.
	 * 
	 * @param accountsDao
	 *            the {@link MockAccountsDao} instance that's also being used
	 */
	public MockGuestLoginIdentitiesDao(MockAccountsDao accountsDao) {
		this.accountsDao = accountsDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao#save(com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity)
	 */
	@Override
	public void save(GuestLoginIdentity login) {
		if (!logins.contains(login)) {
			logins.add(login);
			accountsDao.save(login.getAccount());
		}
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao#getLogins()
	 */
	@Override
	public List<GuestLoginIdentity> getLogins() {
		return logins;
	}
}
