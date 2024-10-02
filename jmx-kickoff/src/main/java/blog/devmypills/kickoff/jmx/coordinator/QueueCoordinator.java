package blog.devmypills.kickoff.jmx.coordinator;

import blog.devmypills.kickoff.jmx.annotation.JmxObject;
import blog.devmypills.kickoff.jmx.consumer.MessageConsumer;
import blog.devmypills.kickoff.jmx.message.Message;
import blog.devmypills.kickoff.jmx.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@JmxObject(name = "blog.devmypills.kickoff.jmx.mbeans:type=QueueCoordinator")
public class QueueCoordinator<T extends Message<?>> implements QueueCoordinatorMBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueCoordinator.class);

	public static final String THREAD_GROUP_NAME = "jmx-kickoff";
	public static final String CONSUMER_THREAD_PREFIX_NAME = "jmx-kickoff-consumer-";
	public static final String PRODUCER_THREAD_PREFIX_NAME = "jmx-kickoff-producer-";

	private final MessageProducer<T> producer;
	private final MessageConsumer<T> consumer;

	private final LinkedBlockingDeque<Thread> producerQueue = new LinkedBlockingDeque<>();
	private final LinkedBlockingDeque<Thread> consumerQueue = new LinkedBlockingDeque<>();
	private final BlockingQueue<T> dataQueue = new LinkedBlockingQueue<>();

	private final AtomicInteger producerCounter = new AtomicInteger(0);
	private final AtomicInteger consumerCounter = new AtomicInteger(0);

	@Override
	public int countMessages() {
		return dataQueue.size();
	}

	@Override
	public int countRunningProducers() {
		return producerQueue.size();
	}

	@Override
	public int countRunningConsumers() {
		return consumerQueue.size();
	}

	@Override
	public void runProducer() {
		try {
			LOGGER.info("Running a new producer. Spinning from thread {}", Thread.currentThread());
			Thread producerThread = new Thread(
					new ThreadGroup(THREAD_GROUP_NAME),
					() -> {
						try {
							while (true) {
								dataQueue.put(producer.produce());
							}
						} catch (Exception ex) {
							Thread.currentThread().interrupt();
							LOGGER.error("Producer thread {}, encounter and error", Thread.currentThread().getName());
							LOGGER.error("Producer error details", ex);
						}
					},
					PRODUCER_THREAD_PREFIX_NAME.concat(String.valueOf(producerCounter.incrementAndGet()))
			);
			producerQueue.put(producerThread);
			producerThread.start();

		} catch (Exception ex) {
			Thread.currentThread().interrupt();
			LOGGER.error("Error trying to add a producer on producer thread queue", ex);
		}
	}

	@Override
	public void stopProducer() {
		LOGGER.info("Stop producer");
		try {
			Optional.of(producerQueue)
					.map(LinkedBlockingDeque::pollLast)
					.ifPresent(Thread::interrupt);
		} catch (Exception ex) {
			LOGGER.error("Polling producer interrupted", ex);
		}
	}

	@Override
	public void runConsumer() {
		try {
			LOGGER.info("Run consumer");
			Thread consumerThread = new Thread(
					new ThreadGroup(THREAD_GROUP_NAME),
					() -> {
						try {
							while (true) {
								T message = dataQueue.poll(1L, TimeUnit.SECONDS);
								consumer.consume(message);
							}
						} catch (Exception ex) {
							Thread.currentThread().interrupt();
							LOGGER.error("Consumer thread {}, encounter and error", Thread.currentThread().getName());
							LOGGER.error("Consumer error details", ex);
						}
					},
					CONSUMER_THREAD_PREFIX_NAME.concat(String.valueOf(consumerCounter.incrementAndGet()))
			);
			consumerQueue.put(consumerThread);
			consumerThread.start();
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
			LOGGER.error("Error trying to add a consumer on consumer thread queue", ex);
		}
	}

	@Override
	public void stopConsumer() {
		LOGGER.info("Stop consumer");
		try {
			Optional.of(consumerQueue)
					.map(LinkedBlockingDeque::pollLast)
					.ifPresent(Thread::interrupt);
		} catch (Exception ex) {
			LOGGER.error("Polling consumer interrupted", ex);
		}
	}

}
