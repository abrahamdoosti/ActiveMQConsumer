
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

/**
 * This is the second way of implementing Active MQ listener
 * 
 * @author abraham
 *
 */
public class Consumer {
	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	public Consumer() {
		try {
			//here we are using a loop back IP, if the broker is on another machine, you need to put that machine's IP
			factory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
			connection = factory.createConnection();
			connection.start();
			System.out.println("connetion started...");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("JCG_QUEUE");
			consumer = session.createConsumer(destination);
            
			//setMessageListener can take a MessageListener implementing class or lambda expression of the Functional Interface method
			consumer.setMessageListener((Message msg)->{
				TextMessage textMessage = (TextMessage) msg;
				try {
					System.out.println("Listening...");
					System.out.println(
							"Consumer " + Thread.currentThread().getName() + " received message: " + textMessage.getText());
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						System.out.println(tm.getText());
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		System.out.println("Starting Consumer...");
		BasicConfigurator.configure();
		Consumer consumer=new Consumer();
		
	}

	private static class HelloMessageListener implements MessageListener {
		public void onMessage(Message msg) {
			TextMessage textMessage = (TextMessage) msg;
			try {
				System.out.println("Listening...");
				System.out.println(
						"Consumer " + Thread.currentThread().getName() + " received message: " + textMessage.getText());
				if (msg instanceof TextMessage) {
					TextMessage tm = (TextMessage) msg;
					System.out.println(tm.getText());
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
