package kodras_stokic.JMS;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.*;
import javax.naming.Context;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Mail implements ManageConnection {

	private String url;
	private String mailbox;

	Context jndiContext;

	private Connection connection;
	private Session session;
	private Queue queue;
	private QueueBrowser browser;
	private MessageProducer producer;

	public Mail(String url, String mailbox) {

		this.url = url;
		this.mailbox = mailbox;
	}

	public void send(String mailbox, String msg) {

		try {
			
			Queue queue_to_send = session.createQueue(mailbox);
			
			producer = session.createProducer(queue_to_send);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			TextMessage textMsg = session.createTextMessage(msg);
			producer.send(textMsg);
			
		} catch(JMSException jmse) {
			
			System.out.println("Error: " + jmse.getMessage());
		}
	}

	public ArrayList<String> getMails() {

		ArrayList<String> mails = new ArrayList<>();

		try {

			Enumeration<?> msgs = browser.getEnumeration();

			while(msgs.hasMoreElements()) {

				TextMessage message = (TextMessage)msgs.nextElement();
				if(message.getText() != null) {

					mails.add(message.getText());
					message.acknowledge();
				}
			}

		} catch(JMSException jmse){

			System.out.println("Error: " + jmse.getMessage());
		}

		return mails;
	}

	@Override
	public void connect() {

		try {

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = session.createQueue(mailbox);
			browser = session.createBrowser(queue);

		}catch(JMSException jmse){

			System.out.println("Error: " + jmse.getMessage());
		}
	}

	@Override
	public void disconnect() {

		try {

			browser.close();
			session.close();
			connection.close();

		} catch (JMSException e) {}
	}
}