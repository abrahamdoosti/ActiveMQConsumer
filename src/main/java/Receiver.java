
import java.io.Reader;
import java.io.StringReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import com.activemq.sender.model.Student;
import com.activemq.sender.model.Students;
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
			factory = new ActiveMQConnectionFactory("tcp://ec2-18-216-2-76.us-east-2.compute.amazonaws.com:61616");
			connection = factory.createConnection();
			connection.start();
			System.out.println("connetion started...");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("JCG_QUEUE");
			consumer = session.createConsumer(destination);
			
			TextMessage tm=null;
			Message msg = consumer.receive(3000);
			System.out.println("Listening...");
			if (msg instanceof TextMessage) {
				 tm = (TextMessage) msg;
				System.out.println(tm.getText());
			} else {
				System.out.println("Queue Empty");
				connection.stop();
				
			}
		
			//create JAXBContext for Students class
			JAXBContext context=JAXBContext.newInstance(Students.class);				
			Unmarshaller studentsUnmarshaller=context.createUnmarshaller();
			
			//store the message in a Reader and pass the reader to unmarshal method so that it can be read and converted to java object
			Reader stringReader=new StringReader(tm.getText());
			
			//we could also create XMLStreamReader and store the message(tm.getText()) and pass the XMLStreamReader in to the unmarshal method		
			//XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(tm.getText()));
			
			Students students=(Students) studentsUnmarshaller.unmarshal(stringReader);
			
			System.out.println(students.getStudentGroupName());
			for(Student stu:students.getStudentList()) {
				System.out.println(stu.toString());
			
			}
			/*while (true) {
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
			}*/
		} catch (JMSException | JAXBException | FactoryConfigurationError e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Receiver receiver = new Receiver();
		receiver.receiveMessage();
		}
}

