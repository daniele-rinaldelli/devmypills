package blog.devmypills.kickoff.jmx.coordinator;

import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.message.Message;
import blog.devmypills.kickoff.jmx.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class QueueCoordinator<T> implements CoordinatorMXBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueCoordinator.class);
	private volatile boolean isProducerRunning = false;
	private volatile boolean isConsumerRunning = false;

	private final MessageProducer<T> producer;
	private final MessageConsumer<T> consumer;

	BlockingQueue<Message<T>> queue = new LinkedBlockingQueue<>();

	@Override
	public int countMessages() {
		return queue.size();
	}

	public void runProducer() {
		LOGGER.info("Run producer");
		isProducerRunning = true;
		new Thread(
				() -> {
					try {
						while (isProducerRunning) {
							Optional<Message<T>> message = producer.produce();
							if (message.isPresent()) {
								queue.put(message.get());
							}
						}
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						LOGGER.error("Producer thread interrupted");
					}
				}
		).start();
	}

	public void stopProducer() {
		LOGGER.info("Stop producer");
		isProducerRunning = false;
	}

	public void runConsumer() {
		LOGGER.info("Run consumer");
		isConsumerRunning = true;
		new Thread(
				() -> {
					try {
						while (isConsumerRunning) {
							Message<T> message = queue.poll(1L, TimeUnit.SECONDS);
							consumer.consume(message);
						}
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						LOGGER.info("Consumer thread interrupted");
					}
				}
		).start();
	}

	public void stopConsumer() {
		LOGGER.info("Stop consumer");
		isConsumerRunning = false;
	}

}
