package org.devocative.samples.j8;

import org.devocative.adroit.date.UniDate;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMisc {

	@Test
	public void testRegEx() {
		final Pattern time = Pattern.compile("(?<H>[0-2]\\d):(?<M>[0-5]\\d):(?<S>[0-5]\\d)");

		Matcher matcher = time.matcher("13:45:23");
		if (matcher.find()) {
			assertEquals("13", matcher.group(1));
			assertEquals("45", matcher.group(2));
			assertEquals("23", matcher.group(3));

			assertEquals("13", matcher.group("H"));
			assertEquals("45", matcher.group("M"));
			assertEquals("23", matcher.group("S"));
		}

		assertEquals("it happened at hour=12 & minute=23", "12:23".replaceAll(
			"(?<H>[0-2]\\d):(?<M>[0-5]\\d)",
			"it happened at hour=${H} & minute=${M}"));
	}

	@Test
	public void testThread() throws Exception {
		AtomicBoolean print = new AtomicBoolean(false);

		Callable<Integer> a = () -> {
			int result = 0;
			for (int i = 0; i < 5; i++) {
				result += i;
				Thread.sleep(1000);
				if (print.get()) {
					System.out.printf("a = %s\n", i);
				}
			}
			return result;
		};

		Callable<Integer> b = () -> {
			int result = 0;
			for (int i = 0; i < 7; i++) {
				result += i;
				Thread.sleep(1500);
				if (print.get()) {
					System.out.printf("b = %s\n", i);
				}
			}
			return result;
		};

		final ExecutorService pool = Executors.newCachedThreadPool();
		ExecutorCompletionService<Integer> ecs = new ExecutorCompletionService<>(pool);
		ecs.submit(a);
		ecs.submit(b);

		UniDate start = UniDate.now();
		System.out.printf("%s: ExecutorCompletionService, After All Submits!\n", start.diff(UniDate.now()).format("H:M:S"));

		int result = ecs.take().get();
		assertTrue(result == 10 || result == 21);
		System.out.printf("%s: R-1-1: %s\n", start.diff(UniDate.now()).format("H:M:S"), result);

		result += ecs.take().get();
		assertEquals(31, result);
		System.out.printf("%s: R-1-2: %s\n", start.diff(UniDate.now()).format("H:M:S"), result);


		System.out.println("---------- invokeAll ----------");
		start = UniDate.now();
		List<Future<Integer>> futures = pool.invokeAll(Arrays.asList(a, b));
		for (Future<Integer> future : futures) {
			result = future.get();
			System.out.printf("%s: R2: %s\n", start.diff(UniDate.now()).format("H:M:S"), result);
			assertTrue(result == 10 || result == 21);
		}

		System.out.println("---------- invokeAny ----------");
		start = UniDate.now();
		print.set(true);
		Integer aResult = pool.invokeAny(Arrays.asList(a, b));
		System.out.printf("%s: aResult = %s\n", start.diff(UniDate.now()).format("H:M:S"), aResult);
		assertTrue(aResult == 10 || aResult == 21);
	}

	// this must volatile
	private MyBool done = new MyBool(false);

	@Test
	public void testThreadVisibility() throws Exception {
		Callable<Integer> hello = () -> {
			for (int i = 1; i <= 1000; i++) {
				System.out.println("Hello " + i);
			}
			done.set(true);
			return 1;
		};

		Callable<Integer> goodbye = () -> {
			int i = 1;
			while (!done.get()) i++;
			System.out.println("Goodbye " + i);
			return i;
		};

		final ExecutorService pool = Executors.newCachedThreadPool();
		final List<Future<Integer>> futures = pool.invokeAll(Arrays.asList(goodbye, hello), 10, TimeUnit.SECONDS);
		for (Future<Integer> future : futures) {
			System.out.println("future.isCancelled() = " + future.isCancelled());
		}
	}

	private static class MyBool {
		// or this must volatile
		private boolean value;

		MyBool(boolean value) {
			this.value = value;
		}

		public boolean get() {
			return value;
		}

		public void set(boolean value) {
			this.value = value;
		}
	}
}
