package org.devocative.samples.spring.bean;

public class DPersistor {
	private String key1;
	private String key2;

	public DPersistor() {
		System.out.println("DPersistor Const!");
	}

	/*
		@Value("#{dConfig.getString('key1')}")
	*/
	public DPersistor setKey1(String key1) {
		System.out.println("DPersistor.setKey1");
		this.key1 = key1;
		return this;
	}

	public DPersistor setKey2(String key2) {
		System.out.println("DPersistor.setKey2");
		this.key2 = key2;
		return this;
	}

	/*
		@PostConstruct
	*/
	public void init() {
		System.out.println("DPersistor.init");
	}

	/*
		@PreDestroy
	*/
	public void shutdown() {
		System.out.println("DPersistor.shutdown");
	}

	public void print() {
		System.out.println("key1 = " + key1);
		System.out.println("key2 = " + key2);
	}
}
