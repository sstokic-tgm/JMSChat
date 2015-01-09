package kodras_stokic.JMS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.JMSException;

public class User extends Thread {

	private String server, user, chatroom, ip, url;
	private MessageChecker msgCheck;
	private Chat chat;
	private Mail mail;

	public User(String server, String user, String chatroom) {

		this.server = server;
		this.user = user;
		this.chatroom = chatroom;

		msgCheck = new MessageChecker();
		
		try {

			getIP();

		} catch (SocketException se) {

			System.out.println("Failed getting the machines local address!\nError: " + se.getMessage());
		}

		url = String.format("failover://tcp://%s:61616", server);
		System.out.println(url);
		System.out.println(ip);

		chat = new Chat(url, chatroom);
		mail = new Mail(url, ip);
	}

	protected void getIP() throws SocketException {

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		while (interfaces.hasMoreElements()) {

			NetworkInterface iface = interfaces.nextElement();
			if (iface.isLoopback() || !iface.isUp())
				continue;

			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while(addresses.hasMoreElements()) {

				InetAddress addr = addresses.nextElement();
				if(!addr.isLoopbackAddress() && addr instanceof Inet4Address)
					this.ip = addr.getHostAddress();
			}
		}
	}

	@Override
	public void run() {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input;

		chat.connect();
		mail.connect();

		chat.startListening();

		boolean exit = false;

		try {

			while(!exit && (input = in.readLine()) != null) {

				MessageType msgType = msgCheck.checkMessage(input);

				switch(msgType) {

				case MAIL_SEND:

					String[] split = input.split(" ");
					StringBuffer msg = new StringBuffer("");

					for(int i = 2; i < split.length; i++) {

						if(i > 2)
							msg.append(' ');

						msg.append(split[i]);
					}

					mail.send(split[1], msg.toString());

					break;

				case MAILBOX:

					ArrayList<String> mails = null;
					mails = mail.getMails();

					for (String mailMsg : mails)
						System.out.println(mailMsg);

					break;

				case MESSAGE_SEND: 

					chat.send(user, ip, input);

					break;

				case EMPTY:

					break;

				case MAILBOX_ERROR:

					System.out.println("usage: MAILBOX");
					break;

				case MAIL_ERROR:

					System.out.println("usage: MAIL <user_ip> <message>");
					break;

				case EXIT:

					exit = true;
					chat.disconnect();
					System.exit(NORM_PRIORITY);
					
					break;
				}
			}

		} catch (IOException ioe) {

			System.out.println("Error: " + ioe.getMessage());
		}
	}
}