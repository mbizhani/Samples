package org.devocative.samples.j8;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestFunP {
	private static final List<String> RESULT_SORTED = Arrays.asList("A", "car", "Bar", "Bill");
	private static final BigDecimal[] RESULT_LEN = new BigDecimal[]{
		BigDecimal.valueOf(3), BigDecimal.ONE, BigDecimal.valueOf(4), BigDecimal.valueOf(3)
	};

	private List<String> strings;
	private final AtomicInteger counter = new AtomicInteger(0);

	// ------------------------------

	@Before
	public void before() {
		strings = Arrays.asList("car", "A", "Bill", "Bar");
	}

	@Test
	public void testLambdaFull() {
		Collections.sort(strings, (String str1, String str2) -> Integer.compare(str1.length(), str2.length()));
		assertEquals(RESULT_SORTED, strings);
	}

	@Test
	public void testLambdaInferred() {
		Collections.sort(strings, (str1, str2) -> Integer.compare(str1.length(), str2.length()));
		assertEquals(RESULT_SORTED, strings);
	}

	@Test
	public void testComparatorLambda() {
		Collections.sort(strings, Comparator.comparingInt(str -> str.length()));
		assertEquals(RESULT_SORTED, strings);
	}

	@Test
	public void testComparatorMethodRefClassInst() {
		Collections.sort(strings, Comparator.comparingInt(String::length)); // ** Class::instanceMethod **
		assertEquals(RESULT_SORTED, strings);
	}

	@Test
	public void testComparatorMethodRefObjectInst() throws InterruptedException {
		Thread.currentThread().setName("MAIN");

		Thread th = new Thread(() -> Collections.sort(strings, this::instComp)); // object::instanceMethod & lambda no param
		th.start();
		th.join();

		assertEquals(RESULT_SORTED, strings);
		assertEquals("MAIN", Thread.currentThread().getName());
	}

	@Test
	public void testComparatorMethodRefEnclosingClassInst() throws Exception {
		Thread th = new Thread(new MyRunner(strings));
		th.start();
		th.join();

		assertEquals(RESULT_SORTED, strings);
		assertEquals(1, counter.get());
	}

	@Test
	public void testConstructorReference() {
		BigDecimal[] allLength = strings
			.stream()
			.map(String::length)          // Class::instanceMethod
			.map(BigDecimal::new)         // Class::constructor
			.toArray(BigDecimal[]::new);  // Array::constructor

		assertArrayEquals(RESULT_LEN, allLength);
	}

	// ------------------------------

	private int instComp(String a, String b) {
		Thread.currentThread().setName("INST_COMP");

		return Integer.compare(a.length(), b.length());
	}

	// ------------------------------

	class MyRunner implements Runnable {
		private List<String> list;

		MyRunner(List<String> list) {
			this.list = list;
		}

		@Override
		public void run() {
			list.sort(TestFunP.this::instComp); // EnclosingClass.this::instanceMethod

			assertEquals("INST_COMP", Thread.currentThread().getName());

			counter.incrementAndGet();
		}
	}
}