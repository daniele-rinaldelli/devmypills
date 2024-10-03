package blog.devmypills.kickoff.jmx.producer;

import blog.devmypills.kickoff.jmx.exception.ProducerInterruptedException;
import blog.devmypills.kickoff.jmx.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface MessageProducer<T extends Message<?>>  {

	Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

	AtomicInteger counter = new AtomicInteger(0);

	T produce();

	Supplier<T> getGenerator();

	default T defaultProduce() {
		return produceAndWait(100, ChronoUnit.MILLIS);
	}

	default T produceAndWait(int interval, ChronoUnit timeUnit) {
		try {
			long millis = Duration.of(interval, timeUnit).toMillis();
			Thread.sleep(millis);
			T message = getGenerator().get();
			counter.getAndIncrement();
			return message;
		} catch (InterruptedException ex) {
			LOGGER.error("Error producing message", ex);
			Thread.currentThread().interrupt();
			throw new ProducerInterruptedException();
		}
	}

	default int getDefaultCounter() {
		return counter.get();
	}

}
