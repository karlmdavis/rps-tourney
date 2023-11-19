package org.rps.tourney.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

/**
 * Provides a simple baseline benchmark for the project.
 */
public class BaselineBenchmarks {
	/**
	 * Provides a baseline benchmark, for empty methods.
	 */
	@Benchmark
	public void emptyMethod() {
		/*
		 * This method is intentionally left empty. It can be used to assess the benchmarks' overhead.
		 */
	}

	/**
	 * This method is only here to allow this {@link Benchmark} class to be run inside Eclipse. These configuration
	 * settings specified in here are only applied within Eclipse.
	 *
	 * @param args
	 *            (not used)
	 * @throws RunnerException
	 *             Any failures in the benchmarks will be wrapped and rethrown as {@link RunnerException}s.
	 */
	public static void main(String[] args) throws RunnerException {
		ChainedOptionsBuilder benchmarkOptions = new OptionsBuilder().include(BaselineBenchmarks.class.getSimpleName())
				.warmupIterations(20).measurementIterations(10).forks(1).threads(10 ^ 2).verbosity(VerboseMode.EXTRA);
		// benchmarkOptions.addProfiler(StackProfiler.class);

		new Runner(benchmarkOptions.build()).run();
	}
}
