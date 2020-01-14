
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;
/**
 * The first way of implementing Active MQ Listener
 * @author abrah
 *
 */
public class Receiver {
	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	public Receiver() {

	}

	public void receiveMessage() {
		try {
			//here we are using a loop back IP, if the broker is on another machine, you need to put that machine's IP
			factory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
			connection = factory.createConnection();
			connection.start();
			System.out.println("connetion started...");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("JCG_QUEUE");
			consumer = session.createConsumer(destination);

			while (true) {
				Message msg = consumer.receive(3000);
				System.out.println("Listening...");
				if (msg instanceof TextMessage) {
					TextMessage tm = (TextMessage) msg;
					System.out.println(tm.getText());
				} else {
					System.out.println("Queue Empty");
					connection.stop();
					break;
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Receiver receiver = new Receiver();
		receiver.receiveMessage();
		}
}

