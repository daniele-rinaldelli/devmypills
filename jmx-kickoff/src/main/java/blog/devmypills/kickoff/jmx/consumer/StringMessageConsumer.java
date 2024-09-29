package blog.devmypills.kickoff.jmx.consumer;

import blog.devmypills.kickoff.jmx.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class StringMessageConsumer implements MessageConsumer<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageConsumer.class);

	@Override
	public void consume(Message<String> message) {
		defaultConsume(message);
	}

	@Override
	public Consumer<Message<String>> getConsumer() {
		return message -> LOGGER.info("Consumed message: {}", message);
	}
}
