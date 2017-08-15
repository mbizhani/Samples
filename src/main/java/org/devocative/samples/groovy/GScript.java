package org.devocative.samples.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Arrays;
import java.util.Map;

public class GScript {
	public static void main(String[] args) throws Exception {
		Binding binding = new Binding();
		binding.setVariable("a", "a");

		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass(DelegatingScript.class.getName());
		GroovyShell sh = new GroovyShell(ClassLoader.getSystemClassLoader(), binding, cc);
		DelegatingScript script = (DelegatingScript) sh.parse(GScript.class.getResource("/org/devocative/samples/groovy/my.dsl.txt").toURI());
		script.setDelegate(new MyDSL());
		script.run();
	}

	public static class MyDSL {
		public Object exec(String commandName, Map<String, Object> params) {
			// String a = (String)binding.getVariable("a");
			return String.format("cmd = %s, params = %s", commandName, params);
		}

		public Object execCl(Closure<ExecDataHandler> cls) {
			ExecDataHandler handler = new ExecDataHandler();
			Closure<ExecDataHandler> rehydrate = cls.rehydrate(handler, null, null);
			rehydrate.setResolveStrategy(Closure.DELEGATE_ONLY);
			rehydrate.call();
			return handler;
		}

		public Object ssh(String prompt, String cmd, boolean force, String... stdin) {
			return String.format("prompt = %s, cmd = %s, force = %s, stdin = %s", prompt, cmd, force, Arrays.toString(stdin));
		}
	}
}
