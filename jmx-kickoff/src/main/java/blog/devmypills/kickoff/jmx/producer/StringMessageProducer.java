package blog.devmypills.kickoff.jmx.producer;

import blog.devmypills.kickoff.jmx.message.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class StringMessageProducer implements MessageProducer<Message<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageProducer.class);

	private final int messageMaxLength;

	@Override
	public Message<String> produce() {
		return defaultProduce();
	}

	@Override
	public Supplier<Message<String>> getGenerator() {
		return () -> {
			Message<String> message = new Message<>(RandomStringUtils.secure().nextAlphabetic(messageMaxLength));
			LOGGER.info("Produced message: {}", message);
			return message;
		};
	}
}
