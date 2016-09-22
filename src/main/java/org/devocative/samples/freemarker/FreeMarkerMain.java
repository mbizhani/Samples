package org.devocative.samples.freemarker;

import freemarker.core.TemplateElement;
import freemarker.ext.beans.StringModel;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FreeMarkerMain {
	public static void main(String[] args) throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<>();
		map.put("name", "World!");
		map.put("i", -1);
		map.put("pr", new Person("John", 25));

		StringWriter out = new StringWriter();

		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setClassicCompatible(true);
		Template template = new Template("test",
			"Hello ${Name}" +

				// (*) The space between "if" and "(" is mandatory!
				// (*) The parentheses are required because of ">" operator
				"<#if (I < 0)>\n" +

				"One\n" +

				"--<#elseif (name??) >\n" +

				"Oops!\n" +

				"--</#if>\n" +
				"|\n" +

				"${pr.name} is ${pr.age} years old :)",
			cfg);
		template.process(map, out, new ObjectWrapper() {
			@Override
			public TemplateModel wrap(Object o) throws TemplateModelException {
				if (o != null) {
					if (o instanceof Map) {
						Map<String, Object> oldMap = (Map<String, Object>) o;
						Map<String, Object> newMap = new HashMap<>();
						for (Map.Entry<String, Object> entry : oldMap.entrySet()) {
							newMap.put(entry.getKey().toLowerCase(), entry.getValue());
						}

						return new SimpleHash((Map) newMap, null) {
							public TemplateModel get(String key) throws TemplateModelException {
								System.out.println("key = " + key);
								return super.get(key.toLowerCase());
							}
						};
					}
				}
				return null;
			}
		});

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

	public static Set<String> referenceSet(Template template) throws TemplateModelException {
		Set<String> result = new HashSet<>();
		TemplateElement rootTreeNode = template.getRootTreeNode();
		for (int i = 0; i < rootTreeNode.getChildCount(); i++) {
			TemplateModel templateModel = rootTreeNode.getChildNodes().get(i);
			if (!(templateModel instanceof StringModel)) {
				continue;
			}
			Object wrappedObject = ((StringModel) templateModel).getWrappedObject();
			if (!"DollarVariable".equals(wrappedObject.getClass().getSimpleName())) {
				continue;
			}

			try {
				Object expression = getInternalState(wrappedObject, "expression");
				switch (expression.getClass().getSimpleName()) {
					case "Identifier":
						result.add(getInternalState(expression, "name").toString());
						break;
					case "DefaultToExpression":
						result.add(getInternalState(expression, "lho").toString());
						break;
					case "BuiltinVariable":
						break;
					default:
						throw new IllegalStateException("Unable to introspect variable");
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new TemplateModelException("Unable to reflect template model");
			}
		}
		return result;
	}

	private static Object getInternalState(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = o.getClass().getDeclaredField(fieldName);
		boolean wasAccessible = field.isAccessible();
		try {
			field.setAccessible(true);
			return field.get(o);
		} finally {
			field.setAccessible(wasAccessible);
		}
	}
}
