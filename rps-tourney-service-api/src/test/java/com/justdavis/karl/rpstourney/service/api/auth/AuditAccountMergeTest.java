package com.justdavis.karl.rpstourney.service.api.auth;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AuditAccountMerge} and {@link AuditAccountGameMerge}
 * (they're always used together).
 */
public final class AuditAccountMergeTest {
	/**
	 * Tests {@link Account#equals(Object)} and {@link Account#hashCode()}.
	 * 
	 * @throws SecurityException
	 *             (won't happen)
	 * @throws NoSuchFieldException
	 *             (won't happen)
	 * @throws IllegalAccessException
	 *             (won't happen)
	 * @throws IllegalArgumentException
	 *             (won't happen)
	 * @throws AddressException
	 *             (shouldn't happen: address is hardcoded)
	 */
	@Test
	public void equalsAndHashCode() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, AddressException {
		// Create the first instance to test with.
		Account account = new Account();
		account.setName("foo");
		Set<AbstractLoginIdentity> mergedLoginsA = new HashSet<>();
		AuditAccountMerge auditEntryA = new AuditAccountMerge(account, mergedLoginsA);

		Assert.assertEquals(auditEntryA, auditEntryA);
		Assert.assertEquals(auditEntryA.hashCode(), auditEntryA.hashCode());

		// Create the second instance to test with.
		Set<AbstractLoginIdentity> mergedLoginsB = new HashSet<>();
		AuditAccountMerge auditEntryB = new AuditAccountMerge(account, mergedLoginsB);

		Assert.assertNotEquals(auditEntryA, auditEntryB);

		/*
		 * The logic for AuditAccountMerge.equals(...) is different for
		 * persisted vs. non-persisted objects, and we want to cover that logic
		 * with this test. To avoid having to involve the DB here (and actually
		 * persist things), we'll cheat and set the fields' values via
		 * reflection.
		 */
		Field auditIdField = AuditAccountMerge.class.getDeclaredField("id");
		auditIdField.setAccessible(true);

		// Create the third instance to test with.
		Set<AbstractLoginIdentity> mergedLoginsC = new HashSet<>();
		AuditAccountMerge auditEntryC = new AuditAccountMerge(account, mergedLoginsC);
		auditIdField.set(auditEntryC, 3);

		Assert.assertEquals(auditEntryC, auditEntryC);
		Assert.assertNotEquals(auditEntryC, auditEntryA);
		Assert.assertNotEquals(auditEntryA, auditEntryC);

		// Create the fourth instance to test with.
		Set<AbstractLoginIdentity> mergedLoginsD = new HashSet<>();
		AuditAccountMerge auditEntryD = new AuditAccountMerge(account, mergedLoginsD);
		auditIdField.set(auditEntryD, 4);

		Assert.assertNotEquals(auditEntryD, auditEntryC);
	}
}
