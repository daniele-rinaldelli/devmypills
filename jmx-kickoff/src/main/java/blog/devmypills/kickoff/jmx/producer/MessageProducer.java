package blog.devmypills.kickoff.jmx.producer;

import blog.devmypills.kickoff.jmx.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface MessageProducer<T> {

	Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

	AtomicInteger counter = new AtomicInteger(0);

	Optional<Message<T>> produce();

	Supplier<T> getGenerator();

	default Optional<Message<T>> defaultProduce() {
		return produceAndWait(100, ChronoUnit.MILLIS);
	}

	default Optional<Message<T>> produceAndWait(int interval, ChronoUnit timeUnit) {
		Optional<Message<T>> message = Optional.empty();
		try {
			long millis = Duration.of(interval, timeUnit).toMillis();
			Thread.sleep(millis);
			Message<T> messageData = new Message<>(getGenerator().get());
			message = Optional.of(messageData);
			counter.getAndIncrement();
		} catch (InterruptedException ex) {
			LOGGER.error("Error producing message", ex);
			Thread.currentThread().interrupt();
		}
		LOGGER.info("Produced message: {}", message);
		return message;
	}

	default int getDefaultCounter() {
		return counter.get();
	}

}
