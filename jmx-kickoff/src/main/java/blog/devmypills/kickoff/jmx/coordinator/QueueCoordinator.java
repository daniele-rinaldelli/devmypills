package blog.devmypills.kickoff.jmx.coordinator;

import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.message.Message;
import blog.devmypills.kickoff.jmx.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class QueueCoordinator<T> implements CoordinatorMXBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueCoordinator.class);

	private final MessageProducer<T> producer;
	private final MessageConsumer<T> consumer;

	private final BlockingQueue<Thread> producerQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<Thread> consumerQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<Message<T>> dataQueue = new LinkedBlockingQueue<>();

	private final AtomicInteger producerCounter = new AtomicInteger(0);
	private final AtomicInteger consumerCounter = new AtomicInteger(0);

	@Override
	public int countMessages() {
		return dataQueue.size();
	}

	@Override
	public void runProducer() {
		try {
			LOGGER.info("Run producer");
			Thread producerThread = new Thread(
					new ThreadGroup("jmx-kickoff"),
					() -> {
						try {
							while (true) {
								dataQueue.put(producer.produce());
							}
						} catch (InterruptedException ex) {
							Thread.currentThread().interrupt();
							LOGGER.error("Putting message interrupted", ex);
						}
					},
					"jmx-kickoff-producer".concat(String.valueOf(producerCounter.incrementAndGet()))
			);
			producerQueue.put(producerThread);
			producerThread.start();

		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			LOGGER.error("Putting producer interrupted", ex);
		} catch (Exception ex) {
			LOGGER.error("Error while producing message", ex);
		}
	}

	//TODO: verify why stopping a producer the application stop

	@Override
	public void stopProducer() {
		LOGGER.info("Stop producer");
		try {
			Thread producer = producerQueue.poll();
			if (producer != null) {
				LOGGER.info("Producer thread {}", producer.getName());
				producer.interrupt();
			}
		} catch (Exception ex) {
			LOGGER.error("Polling producer interrupted", ex);
		}
	}

	@Override
	public void runConsumer() {
		try {
			LOGGER.info("Run consumer");
			Thread consumerThread = new Thread(
					new ThreadGroup("jmx-kickoff"),
					() -> {
						try {
							while (true) {
								Message<T> message = dataQueue.poll(1L, TimeUnit.SECONDS);
								consumer.consume(message);
							}
						} catch (InterruptedException ex) {
							Thread.currentThread().interrupt();
							LOGGER.error("Polling message interrupted", ex);
						}
					},
					"jmx-kickoff-consumer".concat(String.valueOf(consumerCounter.incrementAndGet()))
			);
			consumerQueue.put(consumerThread);
			consumerThread.start();
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
			LOGGER.error("Putting consumer interrupted", ex);
		}
	}

	@Override
	public void stopConsumer() {
		LOGGER.info("Stop consumer");
		try {
			Thread consumer = consumerQueue.poll();
			if (consumer != null) {
				consumer.interrupt();
			}
		} catch (Exception ex) {
			LOGGER.error("Polling consumer interrupted", ex);
		}
	}

}
