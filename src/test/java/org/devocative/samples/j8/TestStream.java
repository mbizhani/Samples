package org.devocative.samples.j8;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
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
			//.collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
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

		final List<String> sortedDistinctWords =
			Arrays.asList("test11", "test02", "test01", "passed", "failed");

		final Path file = Files.createTempFile("__", "__");
		Files.write(file, content.getBytes());

		// Grepping lines containing '1'
		try (Stream<String> lines = Files.lines(file)) {
			String result = lines
				.filter(line -> line.contains("1"))
				.collect(Collectors.joining("\n"));
			assertEquals(grepped, result);
		}

		// List of words
		try (Stream<String> lines = Files.lines(file)) {
			List<String> result = lines
				.flatMap(line -> Stream.of(line.split("\\s")))
				.collect(Collectors.toList());
			assertEquals(words, result);
		}

		// List of distinct words
		try (Stream<String> lines = Files.lines(file)) {
			List<String> result = lines
				.flatMap(line -> Stream.of(line.split("\\s")))
				.distinct()
				.collect(Collectors.toList());
			assertEquals(distinctWords, result);
		}

		// List of distinct & descending-sorted words
		try (Stream<String> lines = Files.lines(file)) {
			List<String> result = lines
				.flatMap(line -> Stream.of(line.split("\\s")))
				.distinct()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
			assertEquals(sortedDistinctWords, result);
		}

		// List of distinct & descending-sorted words
		try (Stream<String> lines = Files.lines(file)) {
			String result = lines
				.flatMap(line -> Stream.of(line.split("\\s")))
				.distinct()
				.sorted(Comparator.reverseOrder())
				.findFirst() // min(Comparator.reverseOrder()) instead of sorted() & findFirst()
				.get();
			assertEquals("test11", result);
		}

		// Count number of words
		try (Stream<String> lines = Files.lines(file)) {
			long result = lines
				.flatMap(line -> Stream.of(line.split("\\s")))
				.count();
			assertEquals(words.size(), result);
		}

		// Count number of characters of words (1/2)
		String fileAsStr = new String(Files.readAllBytes(file));
		long result = Pattern.compile("\\s")
			.splitAsStream(fileAsStr)
			.mapToLong(String::length)
			.sum();
		assertEquals(36, result);

		// Count number of characters of words (2/2)
		fileAsStr = new String(Files.readAllBytes(file));
		result = Pattern.compile("\\s")
			.splitAsStream(fileAsStr)
			.reduce(0L,
				(total, word) -> total + word.length(),
				(total1, total2) -> total1 + total2);
		assertEquals(36, result);
	}

	@Test
	public void testFactorial() {
		long result = LongStream
			//.range(1, 5)        [1, 5)
			.rangeClosed(1, 5) // [1, 5]
			.reduce((left, right) -> left * right)
			.getAsLong();

		assertEquals(120, result);

		result = LongStream
			//.range(1, 5)        [1, 5)
			.rangeClosed(1, 5) // [1, 5]
			.reduce(1, (left, right) -> left * right);

		assertEquals(120, result);
	}

	@Test
	public void testCollectors() {
		List<Employee> list = Arrays.asList(
			new Employee("John", 5000),
			new Employee("Jack", 6000),
			new Employee("Jack", 7000),
			new Employee("Bill", 3000));

		Map<String, Employee> name2employee = list.stream()
			.collect(Collectors.toMap(Employee::getName, Function.identity(), (curV, newV) -> newV));

		assertEquals(3, name2employee.size());
		assertEquals(7000, name2employee.get("Jack").getSalary().intValue());


		final Map<String, List<Employee>> name2employees = list.stream()
			.collect(Collectors.groupingBy(Employee::getName, LinkedHashMap::new, Collectors.toList()));

		assertEquals("John", name2employees.keySet().stream().findFirst().get());
		assertEquals(3, name2employees.size());
		assertEquals(1, name2employees.get("Bill").size());
		assertEquals(2, name2employees.get("Jack").size());


		final int averageSalary = (int) list.stream()
			.mapToInt(Employee::getSalary)
			.average()
			.getAsDouble();
		assertEquals(5250, averageSalary);

		final Map<Boolean, List<Employee>> highSalaryEmployees = list.stream()
			.collect(Collectors.partitioningBy(emp -> emp.getSalary() > averageSalary));

		assertEquals(2, highSalaryEmployees.get(true).size());
		assertEquals(2, highSalaryEmployees.get(false).size());
	}

	// ------------------------------

	class Employee {
		private String name;
		private Integer salary;

		Employee(String name, Integer salary) {
			this.name = name;
			this.salary = salary;
		}

		String getName() {
			return name;
		}

		Integer getSalary() {
			return salary;
		}

		@Override
		public String toString() {
			return getName() + ", " + getSalary();
		}
	}
}
