package org.devocative.samples.spring;

import org.devocative.samples.spring.bean.DPersistor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMain {
	public static void main(String[] args) {
		ApplicationContext appCtx = new ClassPathXmlApplicationContext("application.xml");

		DPersistor persistor = (DPersistor) appCtx.getBean("dPersistor");
		persistor.print();

	}
}
