package blog.devmypills.kickoff.jmx.consumer;

import blog.devmypills.kickoff.jmx.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface MessageConsumer<T> {

	Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

	AtomicInteger counter = new AtomicInteger(0);

	void consume(Message<T> message);

	Consumer<Message<T>> getConsumer();

	default void defaultConsume(Message<T> message) {
		defaultConsumeAtInterval(message, 150, ChronoUnit.MILLIS);
	}

	default void defaultConsumeAtInterval(Message<T> message, int interval, ChronoUnit timeUnit) {
		try {
			long millis = Duration.of(interval, timeUnit).toMillis();
			Thread.sleep(millis);
			getConsumer().accept(message);
			counter.incrementAndGet();
			LOGGER.info("Consumed message: {}", message);

		} catch (InterruptedException ex) {
			LOGGER.error("Error consuming message", ex);
			Thread.currentThread().interrupt();
			throw new RuntimeException("Consumer interrupted");
		}
	}

	default int getDefaultCounter() {
		return counter.get();
	}
}
