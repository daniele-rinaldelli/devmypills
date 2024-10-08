package blog.devmypills.kickoff.jmx.consumer;

import blog.devmypills.kickoff.jmx.exception.ConsumerInterruptedException;
import blog.devmypills.kickoff.jmx.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface MessageConsumer<T extends Message<?>> {

	Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

	AtomicInteger counter = new AtomicInteger(0);

	void consume(T message);

	Consumer<T> getConsumer();

	default void defaultConsume(T message) {
		defaultConsumeAtInterval(message, 150, ChronoUnit.MILLIS);
	}

	default void defaultConsumeAtInterval(T message, int interval, ChronoUnit timeUnit) {
		try {
			long millis = Duration.of(interval, timeUnit).toMillis();
			Thread.sleep(millis);
			getConsumer().accept(message);
			counter.incrementAndGet();
		} catch (InterruptedException ex) {
			LOGGER.error("Consuming message interrupted", ex);
			Thread.currentThread().interrupt();
			throw new ConsumerInterruptedException();
		}
	}

	default int getDefaultCounter() {
		return counter.get();
	}
}
