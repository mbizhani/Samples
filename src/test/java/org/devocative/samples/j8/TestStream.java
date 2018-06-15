package org.devocative.samples.j8;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class TestStream {
	@Test
	public void testFibonacci() {
		Stream<int[]> iterate;

		iterate = Stream.iterate(new int[]{1, 1}, n -> new int[]{n[1], n[0] + n[1]});
		int nth = iterate
			.peek(n -> System.out.printf("Debug: %s \n", Arrays.toString(n)))
			.limit(5)
			.reduce((a, b) -> b)
			.get()[1];
		assertEquals(8, nth);

		iterate = Stream.iterate(new int[]{1, 1}, n -> new int[]{n[1], n[0] + n[1]});
		List<Integer> list = iterate
			.limit(5)
			.map(n -> n[1])
			.collect(Collectors.toList());
		assertEquals(list, Arrays.asList(1, 2, 3, 5, 8));
	}

	@Test
	public void test_Files_FlatMap_Distinct_Sorted_Reduction() throws IOException {
		final String content = "test01 passed\ntest02 passed\ntest11 failed";
		final String grepped = "test01 passed\ntest11 failed";

		final List<String> words =
			Arrays.asList("test01", "passed", "test02", "passed", "test11", "failed");

		final List<String> distinctWords =
			Arrays.asList("test01", "passed", "test02", "test11", "failed");

		final List<String> sortedWords =
			Arrays.asList("test11", "test02", "test01", "passed", "failed");

		final Path file = Files.createTempFile("__", "__");
		Files.write(file, content.getBytes());

		// Grepping lines containing '1'
		try (Stream<String> stream = Files.lines(file)) {
			String result = stream
				.filter(line -> line.contains("1"))
				.collect(Collectors.joining("\n"));
			assertEquals(grepped, result);
		}

		// List of words
		try (Stream<String> stream = Files.lines(file)) {
			List<String> result = stream
				.flatMap(line -> Stream.of(line.split("\\s")))
				.collect(Collectors.toList());
			assertEquals(words, result);
		}

		// List of distinct words
		try (Stream<String> stream = Files.lines(file)) {
			List<String> result = stream
				.flatMap(line -> Stream.of(line.split("\\s")))
				.distinct()
				.collect(Collectors.toList());
			assertEquals(distinctWords, result);
		}

		// List of distinct & descending sorted words
		try (Stream<String> stream = Files.lines(file)) {
			List<String> result = stream
				.flatMap(line -> Stream.of(line.split("\\s")))
				.distinct()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
			assertEquals(sortedWords, result);
		}

		// Count number of words
		try (Stream<String> stream = Files.lines(file)) {
			long result = stream
				.flatMap(line -> Stream.of(line.split("\\s")))
				.count();
			assertEquals(words.size(), result);
		}

		// Count number of characters of words
		try (Stream<String> stream = Files.lines(file)) {
			long result = stream
				.flatMap(line -> Stream.of(line.split("\\s")))
				.mapToLong(String::length)
				.sum();
			assertEquals(36, result);
		}

		IntStream.of(1, 2, 3);

	}
}
