package blog.devmypills;

import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.consumer.StringMessageConsumer;
import blog.devmypills.kickoff.jmx.coordinator.CoordinatorMXBean;
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

	public static void main(String[] args) {
		LOGGER.info("JMX Kickoff");
		new App().execute();
	}

	private void execute() {
		try {
			MessageProducer<String> producer = new StringMessageProducer(20);
			MessageConsumer<String> consumer = new StringMessageConsumer();

			QueueCoordinator<String> queueCoordinator = new QueueCoordinator<>(producer, consumer);

			queueCoordinator.runProducer();
			queueCoordinator.runConsumer();

			instrumentCoordinator(queueCoordinator);
		} catch (Exception ex) {
			LOGGER.error("Error", ex);
		}
	}

	private void instrumentCoordinator(CoordinatorMXBean coordinator) {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName managedObjectName = new ObjectName("blog.devmypills.kickoff.jmx.mbeans:type=QueueCoordinator");
			mBeanServer.registerMBean(coordinator, managedObjectName);
			LOGGER.info("Instrumentation completed");
		} catch (Exception ex) {
			LOGGER.error("Error while instrumenting mbeans", ex);
		}
	}

}
