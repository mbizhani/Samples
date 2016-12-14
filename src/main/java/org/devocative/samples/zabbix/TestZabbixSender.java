package org.devocative.samples.zabbix;

import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;

import java.io.IOException;
import java.util.Calendar;

public class TestZabbixSender {
	public static void main(String[] args) throws IOException {
		String host = "172.16.1.51";
		int port = 10051;
		ZabbixSender zabbixSender = new ZabbixSender(host, port);

		DataObject dataObject = new DataObject();
		dataObject.setHost("test");
		dataObject.setKey("test_item");

		Calendar cal = Calendar.getInstance();
		for (int i = 1; i < 300; i++) {
			cal.add(Calendar.DATE, -1);

			System.out.println(cal.getTime());

			int value = (int) (Math.random() * 100);
			dataObject.setValue(String.valueOf(value));

			// TimeUnit is SECONDS.
			dataObject.setClock(cal.getTimeInMillis() / 1000);
			SenderResult result = zabbixSender.send(dataObject);

			System.out.println("result:" + result);
			if (result.success()) {
				System.out.println("send success.");
			} else {
				System.err.println("send fail!");
				break;
			}
		}
	}
}
