package blog.devmypills.kickoff.jmx.producer;

import blog.devmypills.kickoff.jmx.message.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class StringMessageProducer implements MessageProducer<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageProducer.class);

	private final int length;

	@Override
	public Optional<Message<String>> produce() {
		return defaultProduce();
	}

	@Override
	public Supplier<String> getGenerator() {
		return () -> RandomStringUtils.secure().nextAlphabetic(length);
	}
}
