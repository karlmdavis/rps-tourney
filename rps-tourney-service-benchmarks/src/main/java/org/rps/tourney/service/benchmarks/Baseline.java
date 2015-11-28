package org.rps.tourney.service.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * Provides a simple baseline benchmark for the project.
 */
public class Baseline {
	/**
	 * Provides a baseline benchmark, for empty methods.
	 */
	@Benchmark
	public void emptyMethod() {
		/*
		 * This method is intentionally left empty. It can be used to assess the
		 * benchmarks' overhead.
		 */
	}
}
