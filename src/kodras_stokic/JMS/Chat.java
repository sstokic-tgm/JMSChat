package kodras_stokic.JMS;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Chat extends Thread implements ManageConnection {

	private String url;
	private String subject;

	private boolean connected;

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public Chat(String url, String subject) {

		this.url = url;
		this.subject = subject;

		connected = false;
	}

	public void startListening() {

		start();
	}
	
	public void send(String user, String ip, String msg) {
		
		TextMessage textMsg;
		
		try {
			
			textMsg = session.createTextMessage(user + " " + ip + ": " + msg);
			producer.send(textMsg);
			
		} catch (JMSException jmse) {
			
			System.out.println("Error: " + jmse.getMessage());
		}
		
	}

	@Override
	public void run() {

		try {

			while(true) {


				TextMessage msg = (TextMessage)consumer.receive();

				if(msg != null) {

					System.out.println(msg.getText());
					msg.acknowledge();
				}
			}

		} catch (JMSException jmse) {

			if(connected == true)
				System.out.println("Error: " + jmse.getMessage());
		}
	}

	@Override
	public void connect() {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

		try {

			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(subject);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			consumer = session.createConsumer(destination);

			connected = true;

		} catch (JMSException e) {

			System.out.println("Could not connect to the server!");
			System.exit(1);
		}
	}

	@Override
	public void disconnect() {

		connected = false;

		try {

			producer.close();
			consumer.close();
			session.close();
			connection.close();

		} catch (JMSException e) { }
	}
}