package blog.devmypills;

import blog.devmypills.kickoff.jmx.annotation.JmxObject;
import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.consumer.StringMessageConsumer;
import blog.devmypills.kickoff.jmx.coordinator.QueueCoordinator;
import blog.devmypills.kickoff.jmx.coordinator.QueueCoordinatorMBean;
import blog.devmypills.kickoff.jmx.message.Message;
import blog.devmypills.kickoff.jmx.producer.MessageProducer;
import blog.devmypills.kickoff.jmx.producer.StringMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Objects;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		LOGGER.info("JMX Kickoff");
		new App().execute();
	}

	private void execute() {
		try {
			MessageProducer<Message<String>> producer = new StringMessageProducer(20);
			MessageConsumer<Message<String>> consumer = new StringMessageConsumer();

			QueueCoordinator<Message<String>> queueCoordinator = new QueueCoordinator<>(producer, consumer);

			queueCoordinator.runProducer();
			queueCoordinator.runConsumer();

			instrumentCoordinator(queueCoordinator);

			LOGGER.info("Main thread terminated");

		} catch (Exception ex) {
			LOGGER.error("Error", ex);
		}
	}

	private void instrumentCoordinator(QueueCoordinatorMBean coordinator) {
		try {
			JmxObject jmxMetadata = coordinator.getClass().getAnnotation(JmxObject.class);
			if (Objects.nonNull(jmxMetadata)) {
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				ObjectName managedObject = new ObjectName(jmxMetadata.name());
				mBeanServer.registerMBean(coordinator, managedObject);
				LOGGER.info("Instrumentation completed");
			}
		} catch (Exception ex) {
			LOGGER.error("Error while instrumenting mbeans", ex);
		}
	}
}
