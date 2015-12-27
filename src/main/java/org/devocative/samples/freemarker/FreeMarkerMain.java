package org.devocative.samples.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerMain {
	public static void main(String[] args) throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<>();
		map.put("name", "World!");
		map.put("i", 1);
		map.put("pr", new Person("John", 25));

		StringWriter out = new StringWriter();

		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		Template template = new Template("test",
			"Hello ${name}" +

				// (*) The space between "if" and "(" is mandatory!
				// (*) The parentheses are required because of ">" operator
				"<#if (i > 0)>" +

				"One" +
				"<#else>" +
				"Oops!" +
				"</#if>" +
				"|\n" +

				"${pr.name} is ${pr.age} years old :)",
			cfg);
		template.process(map, out);

		System.out.println(out.toString());
	}

	public static class Person {
		private String name;
		private Integer age;

		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public Integer getAge() {
			return age;
		}
	}
}
