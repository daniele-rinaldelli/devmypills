package blog.devmypills;

import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.consumer.StringMessageConsumer;
import blog.devmypills.kickoff.jmx.coordinator.QueueCoordinator;
import blog.devmypills.kickoff.jmx.producer.MessageProducer;
import blog.devmypills.kickoff.jmx.producer.StringMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws InterruptedException {
		LOGGER.info("JMX Kickoff");
		App app = new App();
		app.execute();
	}

	private void execute() throws InterruptedException {

		MessageProducer<String> producer = new StringMessageProducer(20);
		MessageConsumer<String> consumer = new StringMessageConsumer();

		QueueCoordinator<String> queueCoordinator = new QueueCoordinator<>(producer, consumer);

		queueCoordinator.runProducer();

		Thread.sleep(5000);
//		queueCoordinator.stopProducer();

		queueCoordinator.runConsumer();

		Thread.sleep(5000);
		queueCoordinator.stopConsumer();

		instrumentManagement(queueCoordinator);

		LOGGER.info("N. of messages available: {}", queueCoordinator.countMessages());
	}

	private void instrumentManagement(QueueCoordinator queueCoordinator) {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName managedObjectName = new ObjectName("edu.danir.kickoff.jmx.mbeans:type=QueueCoordinator");
			mBeanServer.registerMBean(queueCoordinator, managedObjectName);
			LOGGER.info("Instrumentation completed");
		} catch (Exception ex) {
			LOGGER.error("Error while instrumenting mbeans", ex);
		}
	}

}
