package org.devocative.samples.j8;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestManyToMany {

	@Test
	public void test() {
		Group scm = new Group("SCM");
		Group mtc = new Group("MTC");
		Group hse = new Group("HSE");

		Report stock = new Report("Stock Inventory", scm, mtc);
		Report incid = new Report("Incidents", hse);
		Report artcl = new Report("Accounting Articles", scm, mtc, hse);
		Report mttr = new Report("MTTR", mtc);

		List<Report> reports = Arrays.asList(stock, incid, artcl, mttr);

		Map<Group, List<Report>> expected = new TreeMap<>();
		expected.put(scm, Arrays.asList(stock, artcl));
		expected.put(mtc, Arrays.asList(stock, artcl, mttr));
		expected.put(hse, Arrays.asList(incid, artcl));


		Map<Group, List<Report>> result = reports.stream()
			.flatMap(report ->
				report.getGroups().stream().map(dataGroup ->
					new AbstractMap.SimpleEntry<>(dataGroup, report)
				)
			)
			.collect(Collectors.groupingBy(
				AbstractMap.SimpleEntry::getKey,
				TreeMap::new,
				Collectors.mapping(
					AbstractMap.SimpleEntry::getValue,
					Collectors.toList()))
			);

		assertEquals(expected, result);
	}

	// ------------------------------

	private class Report {
		private String name;
		private List<Group> groups;

		Report(String name, Group... groups) {
			this.name = name;
			this.groups = Arrays.asList(groups);
		}

		String getName() {
			return name;
		}

		List<Group> getGroups() {
			return groups;
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	// ------------------------------

	private class Group implements Comparable<Group> {
		private String name;

		Group(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public int compareTo(Group o) {
			return getName().compareTo(o.getName());
		}
	}
}