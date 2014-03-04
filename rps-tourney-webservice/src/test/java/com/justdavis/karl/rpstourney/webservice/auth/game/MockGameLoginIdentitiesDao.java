package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.MockAccountsDao;
import com.justdavis.karl.rpstourney.webservice.auth.guest.IGuestLoginIndentitiesDao;

/**
 * A mock {@link IGuestLoginIndentitiesDao} implementation for use in tests.
 * Stores {@link GuestLoginIdentity} instances in-memory.
 */
public final class MockGameLoginIdentitiesDao implements
		IGameLoginIndentitiesDao {
	public final List<GameLoginIdentity> logins = new ArrayList<>();
	private final MockAccountsDao accountsDao;

	/**
	 * Constructs a new {@link MockGameLoginIdentitiesDao} instance.
	 * 
	 * @param accountsDao
	 *            the {@link MockAccountsDao} instance that's also being used
	 */
	public MockGameLoginIdentitiesDao(MockAccountsDao accountsDao) {
		this.accountsDao = accountsDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.guest.IGuestLoginIndentitiesDao#save(com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity)
	 */
	@Override
	public void save(GameLoginIdentity login) {
		if (!logins.contains(login)) {
			logins.add(login);
			accountsDao.save(login.getAccount());
		}
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.game.IGameLoginIndentitiesDao#find(javax.mail.internet.InternetAddress)
	 */
	@Override
	public GameLoginIdentity find(InternetAddress emailAddress) {
		for (GameLoginIdentity login : logins)
			if (login.getEmailAddress().equals(emailAddress))
				return login;

		return null;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.guest.IGuestLoginIndentitiesDao#getLogins()
	 */
	@Override
	public List<GameLoginIdentity> getLogins() {
		return logins;
	}
}
