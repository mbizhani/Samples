package org.devocative.samples.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Shell {
	static JSch jsch = new JSch();
	static Session session;

	public static void main(String[] args) throws JSchException, IOException {
		createSession("root", "qazwsx@123", "172.16.1.243");
//		exec("ls -l");
		exec("df -f");
//		exec("pwd");
//		exec("mkdir test");
//		exec("ll /asd");
//		exec(session, "ps ax|grep httpd| grep -v grep");
//		exec(session, "echo $PATH\nls -l\ndf -h\nps ax|grep httpd| grep -v grep");
		//exec(session, "ping 172.16.1.200");
		session.disconnect();

//		session = createSession("root", "qazwsx@123", "172.16.4.3");
//		exec(session, "vim-cmd vmsvc/getallvms");
//		exec(session, "vim-cmd vmsvc/power.on 1");
//		session.disconnect();
	}

	static void createSession(String username, String password, String host) throws JSchException {
		session = jsch.getSession(username, host, 22);
		session.setPassword(password);

		// It must not be recommended, but if you want to skip host-key check,
		// invoke following,
		session.setConfig("StrictHostKeyChecking", "no");

		session.connect(30000); // making a connection with timeout.

	}

	static void exec(String cmd) throws JSchException, IOException {

		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);

		InputStream in = channelExec.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		channelExec.connect();

		while (true) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			if (channelExec.isClosed()) {
				System.out.println("--- exit-status: " + channelExec.getExitStatus());
				break;
			}
			/*try {
				Thread.sleep(1000);
				System.out.println("--- sleep");
			} catch (Exception ee) {
				ee.printStackTrace();
			}*/
		}

		channelExec.disconnect();
	}
}
