package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;

/**
 * <p>
 * Integration tests for {@link ConsoleApp}.
 * </p>
 */
public final class ConsoleAppIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleAppIT.class);

	/**
	 * Runs the app, creating a new local game against an AI opponent. The "human" player in the test will make the same
	 * throw over and over until the game ends. It's a bit nondeterministic, but the best we can do without allowing
	 * users to specify one of the deterministic AIs (which would be a bad idea).
	 *
	 * @throws AddressException
	 *             (won't happen; addresses are hardcoded)
	 * @throws IOException
	 *             (indicates problem with test code)
	 */
	@Test(timeout = 5000)
	public void playOneRoundGameAgainstLocalAi() throws AddressException, IOException {
		final ByteArrayOutputStream gameOutputStream = new ByteArrayOutputStream();
		final MockInputStream gameInputStream = new MockInputStream();

		/*
		 * Because we need to review the output and then respond to it (via the input), the game must be run on a
		 * separate thread. This thread will block whenever it's waiting for new input. When the MockInputStream.exit()
		 * method is called, it will stop blocking and just return -1 after exhausting all existing input.
		 */
		ExecutorService gameExecutor = Executors.newSingleThreadExecutor();
		Runnable gameRunnable = new GameRunnable(gameInputStream, gameOutputStream, "--ai", "Hard", "-r", "1");

		// Start the game.
		Future<?> gameFuture = gameExecutor.submit(gameRunnable);

		// Play rounds until the game ends.
		while (!gameFuture.isDone()) {
			String gameOutputSoFar = gameOutputStream.toString(StandardCharsets.US_ASCII.name());
			if (gameOutputSoFar.toString().endsWith("Select your throw [R,P,S]: ")) {
				// Make a Throw (doesn't matter what).
				gameInputStream.addMessage("R\n");
			}
		}

		// Not required, but good to "clean up" anyways.
		gameInputStream.exit();

		// Verify that the user either won or lost.
		String gameOutput = gameOutputStream.toString(StandardCharsets.US_ASCII.name());
		LOGGER.info("Final game output:\n" + gameOutput);
		Assert.assertTrue(gameOutput.contains("You are playing against Challenging. Best out of 1 wins!"));
		Assert.assertTrue(gameOutput.contains("Final Score"));
		Assert.assertTrue(gameOutput.contains("You won!") || gameOutput.contains("You lost."));
	}

	/**
	 * Runs the app, creating a new online game against an AI opponent. The "human" player in the test will make the
	 * same throw over and over until the game ends. It's a bit nondeterministic, but the best we can do without
	 * allowing users to specify one of the deterministic AIs (which would be a bad idea).
	 *
	 * @throws AddressException
	 *             (won't happen; addresses are hardcoded)
	 * @throws IOException
	 *             (indicates problem with test code)
	 */
	@Test(timeout = 5000)
	public void playOneRoundGameAgainstNetworkAi() throws AddressException, IOException {
		// Use the web service to create a login that can be used.
		final String userEmail = String.format("foo.%d@example.com", new Random().nextInt());
		final String userPassword = "secret";
		GameAuthClient gameAuthClient = new GameAuthClient(ITUtils.createClientConfig(), new CookieStore());
		gameAuthClient.createGameLogin(new InternetAddress(userEmail), userPassword);

		final ByteArrayOutputStream gameOutputStream = new ByteArrayOutputStream();
		final MockInputStream gameInputStream = new MockInputStream();

		/*
		 * Because we need to review the output and then respond to it (via the input), the game must be run on a
		 * separate thread. This thread will block whenever it's waiting for new input. When the MockInputStream.exit()
		 * method is called, it will stop blocking and just return -1 after exhausting all existing input.
		 */
		ExecutorService gameExecutor = Executors.newSingleThreadExecutor();
		Runnable gameRunnable = new GameRunnable(gameInputStream, gameOutputStream, "--online", "--server",
				ITUtils.createClientConfig().getServiceRoot().toString(), "--user", userEmail, "--password",
				userPassword, "--ai", "Hard", "-r", "1");

		// Start the game.
		Future<?> gameFuture = gameExecutor.submit(gameRunnable);

		// Play rounds until the game ends.
		while (!gameFuture.isDone()) {
			String gameOutputSoFar = gameOutputStream.toString(StandardCharsets.US_ASCII.name());
			if (gameOutputSoFar.toString().endsWith("Select your throw [R,P,S]: ")) {
				// Make a Throw (doesn't matter what).
				gameInputStream.addMessage("R\n");
			}
		}

		// Not required, but good to "clean up" anyways.
		gameInputStream.exit();

		// Verify that the user either won or lost.
		String gameOutput = gameOutputStream.toString(StandardCharsets.US_ASCII.name());
		LOGGER.info("Final game output:\n" + gameOutput);
		Assert.assertTrue(gameOutput.contains("You are playing against Challenging. Best out of 1 wins!"));
		Assert.assertTrue(gameOutput.contains("Final Score"));
		Assert.assertTrue(gameOutput.contains("You won!") || gameOutput.contains("You lost."));
	}

	/**
	 * Used to run games on a separate thread.
	 */
	private final class GameRunnable implements Runnable {
		private final InputStream gameInputStream;
		private final ByteArrayOutputStream gameOutputStream;
		private final String[] gameArgs;

		/**
		 * Constructs a new {@link GameRunnable} instance.
		 *
		 * @param gameInputStream
		 *            the {@link InputStream} that will be used to interact with the game
		 * @param gameOutputStream
		 *            the {@link ByteArrayOutputStream} that the game will display to
		 * @param gameArgs
		 *            the list of arguments to pass to {@link ConsoleApp#runApp(String[], PrintStream, InputStream)}
		 */
		private GameRunnable(InputStream gameInputStream, ByteArrayOutputStream gameOutputStream, String... gameArgs) {
			this.gameInputStream = gameInputStream;
			this.gameOutputStream = gameOutputStream;
			this.gameArgs = gameArgs;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			ConsoleApp app = new ConsoleApp();
			try {
				app.runApp(gameArgs, new PrintStream(gameOutputStream), gameInputStream);
			} catch (Exception e) {
				/*
				 * Catch and rethrow errors, also logging the console output to that point. Otherwise, things are
				 * impossible to debug.
				 */
				try {
					LOGGER.error("Test failed. Game output:\n"
							+ gameOutputStream.toString(StandardCharsets.US_ASCII.name()));
				} catch (UnsupportedEncodingException e1) {
					// Won't happen; encoding is hardcoded.
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * A mock {@link InputStream} that tests can dynamically provide content to.
	 */
	private static final class MockInputStream extends InputStream {
		private static final Logger LOGGER = LoggerFactory.getLogger(MockInputStream.class);
		private static final StringReader MAGIC_OBJECT_EXIT = new StringReader("");

		private final BlockingQueue<StringReader> messages;
		private StringReader currentMessage;

		/**
		 * Constructs a new {@link MockInputStream} instance.
		 */
		public MockInputStream() {
			this.messages = new LinkedBlockingQueue<>();
			this.currentMessage = null;
		}

		/**
		 * Adds a new message to the queue of those to be read through.
		 *
		 * @param message
		 *            the new message to read (messages are read in FIFO order)
		 */
		public void addMessage(String message) {
			this.messages.add(new StringReader(message));
		}

		/**
		 * After calling this method, {@link #read()} will no longer block if no further messages are (yet) available to
		 * be read.
		 */
		public void exit() {
			/*
			 * Per the suggestion in BlockingQueue's javadoc, we use a "magic object" message to signal that we should
			 * stop waiting for new content.
			 */
			this.messages.offer(MAGIC_OBJECT_EXIT);
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			// Get the next result from the current message (if any).
			int nextResult = currentMessage != null ? currentMessage.read() : -1;

			// If there was no result, grab the next message.
			if (nextResult == -1) {
				/*
				 * This stream is read on a different thread than the one used to construct and add content to it. The
				 * moveToNextMessage() call will block the read() thread until a message is available, or until exit()
				 * has been called.
				 */
				moveToNextMessage();
				nextResult = currentMessage.read();
			}

			return nextResult;
		}

		/**
		 * Waits for and/or moves to the next message. Adjusts {@link #messages} and {@link #currentMessage}.
		 */
		private void moveToNextMessage() {
			if (this.currentMessage == MAGIC_OBJECT_EXIT)
				return;

			try {
				this.currentMessage = this.messages.take();
			} catch (InterruptedException e) {
				/*
				 * If this thread is interrupted (whether or not it's currently waiting), that likely means someone is
				 * asking us to shut down.
				 */
				LOGGER.warn("Interrupted, so closing down this stream.", e);
				this.exit();
				this.currentMessage = this.messages.poll();
			}
		}
	}
}
