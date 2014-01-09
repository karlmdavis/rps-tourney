package com.justdavis.karl.rpstourney.webservice.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Just a sample/test JPA {@link Entity}.
 */
@Entity
public final class FooEntity {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;
	private final String title;

	/**
	 * This default, no-arg constructor is required by JPA.
	 */
	@SuppressWarnings("unused")
	private FooEntity() {
		this.title = null;
	}

	/**
	 * Constructs a new {@link FooEntity} instance.
	 * 
	 * @param title
	 *            the value to use for {@link #getTitle()}
	 */
	public FooEntity(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
}
