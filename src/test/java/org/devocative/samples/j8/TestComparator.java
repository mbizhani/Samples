package org.devocative.samples.j8;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestComparator {
	@Test
	public void testComparator() {
		List<Employee> list = Arrays.asList(
			new Employee("John", 5000),
			new Employee("Jack", 5000),
			new Employee("Bill", 3000));

		List<String> expected = Arrays.asList("Jack, 5000", "John, 5000", "Bill, 3000");

		// try to sort: first by salary descending, and then by name ascending
		list.sort(Comparator
			.comparing(Employee::getSalary, (o1, o2) -> Double.compare(o2, o1))
			.thenComparing(Employee::getName));

		List<String> actual = list.stream().map(Employee::toString).collect(Collectors.toList());

		assertEquals(expected, actual);
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
