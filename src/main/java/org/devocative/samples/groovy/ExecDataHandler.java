package org.devocative.samples.groovy;

import java.util.Arrays;
import java.util.Map;

public class ExecDataHandler {
	private String prompt;
	private String name;
	private Map<String, Object> params;
	private Integer[] list;

	public void prompt(String prompt) {
		this.prompt = prompt;
	}

	public void name(String name) {
		this.name = name;
	}

	public void params(Map<String, Object> params) {
		this.params = params;
	}

	public void plist(Integer[] list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ExecDataHandler{" +
			"prompt='" + prompt + '\'' +
			", name='" + name + '\'' +
			", params=" + params +
			", list=" + Arrays.toString(list) +
			'}';
	}
}
