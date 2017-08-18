package org.devocative.samples.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import groovy.util.Proxy;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GScript {
	public static void main(String[] args) throws Exception {
		int max = 1;

		long scriptGen = 0;
		long scriptExec = 0;

		for (int i = 0; i < max; i++) {
			long start = System.currentTimeMillis();

			Binding binding = new Binding();
			binding.setVariable("a", i);

			CompilerConfiguration cc = new CompilerConfiguration();
			cc.setScriptBaseClass(DelegatingScript.class.getName());
			GroovyShell sh = new GroovyShell(ClassLoader.getSystemClassLoader(), cc);
			DelegatingScript script = (DelegatingScript) sh.parse(GScript.class.getResource("/org/devocative/samples/groovy/my.dsl.txt").toURI());

			long gen = System.currentTimeMillis();

			//script.setDelegate(new MyDSL(binding.getVariables()));
			script.setBinding(binding);
			script.setDelegate(new MyDynamicDSL(new MyDSL(binding.getVariables())));
			script.run();

			long exec = System.currentTimeMillis();

			scriptGen += gen - start;
			scriptExec += exec - gen;
		}

		System.out.println("scriptGen = " + (scriptGen / max));
		System.out.println("scriptExec = " + (scriptExec / max));

	}

	public static class MyDSL {
		private Map binding;

		public MyDSL(Map binding) {
			this.binding = binding;
		}

		public Object exec(CharSequence cmd) {
			return exec(cmd, new HashMap<String, Object>());
		}

		public Object exec(CharSequence cmd, Map<String, Object> params) {
			// String a = (String)binding.getVariable("a");
			return String.format("cmd = %s, params = %s", cmd, params);
		}

		public Object execCl(Closure cls) {
			MapOfClosureDelegate delegate = new MapOfClosureDelegate();
			Closure rehydrate = cls.rehydrate(delegate, binding, null);
			rehydrate.setResolveStrategy(Closure.DELEGATE_FIRST);
			rehydrate.call();
			return delegate.getClosureAsMap();

			/*ExecDataHandler handler = new ExecDataHandler();
			Closure<ExecDataHandler> rehydrate = cls.rehydrate(handler, binding, null);
			rehydrate.setResolveStrategy(Closure.DELEGATE_FIRST);
			rehydrate.call();
			return handler;*/
		}

		public Object ssh(CharSequence prompt, CharSequence cmd, Boolean force, CharSequence... stdin) {
			return ((int) (Math.random() * 1000));
		}
	}

	public static class MyDynamicDSL extends Proxy {
		private MyDSL dsl;

		public MyDynamicDSL(MyDSL dsl) {
			this.dsl = dsl;
		}

		@Override
		public Object invokeMethod(String name, Object args) {
			Object[] argsArr = (Object[]) args;

			try {
				return callMethod(name, argsArr);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
			return String.format("Other method: %s - %s", name, Arrays.toString(argsArr));
		}

		private Object callMethod(String name, Object[] args) throws Exception {
			Method result = null;
			Object[] finalArgs = null;

			Method[] methods = dsl.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(name)) {
					Parameter[] parameters = method.getParameters();

					int varArgIdx = -1;
					int paramCount = parameters.length;
					if (paramCount > 0 && parameters[paramCount - 1].isVarArgs()) {
						varArgIdx = paramCount - 1;
						paramCount--;
					}

					if (args.length >= paramCount || varArgIdx > 0) {
						finalArgs = new Object[parameters.length];
						boolean allParams = true;

						for (int i = 0; i < paramCount; i++) {
							allParams = allParams && parameters[i].getType().isAssignableFrom(args[i].getClass());
							finalArgs[i] = args[i];
						}

						if (varArgIdx > 0) {
							Parameter varArg = parameters[varArgIdx];
							Object[] varArgParams;
							if (args.length > varArgIdx) {
								varArgParams = (Object[]) Array.newInstance(varArg.getType().getComponentType(), args.length - varArgIdx);
								for (int i = varArgIdx; i < args.length; i++) {
									allParams = allParams && varArg.getType().getComponentType().isAssignableFrom(args[i].getClass());
									varArgParams[i - varArgIdx] = args[i];
								}
							} else {
								varArgParams = (Object[]) Array.newInstance(varArg.getType().getComponentType(), 0);
							}

							finalArgs[varArgIdx] = varArgParams;
						}

						if (allParams) {
							result = method;
							break;
						}
					}
				}
			}

			if (result != null) {
				return result.invoke(dsl, finalArgs);
			}
			throw new NoSuchMethodException(name);
		}
	}

	public static class MapOfClosureDelegate extends Proxy {
		private Map<String, Object> closureAsMap = new LinkedHashMap<>();

		public Map<String, Object> getClosureAsMap() {
			return closureAsMap;
		}

		@Override
		public Object invokeMethod(String name, Object args) {
			Object[] argsArr = (Object[]) args;
			if (argsArr.length == 1) {
				closureAsMap.put(name, argsArr[0]);
			} else {
				closureAsMap.put(name, argsArr);
			}
			return null;
		}
	}
}
