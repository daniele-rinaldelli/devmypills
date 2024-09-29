package blog.devmypills.kickoff.jmx.producer;

import blog.devmypills.kickoff.jmx.message.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class StringMessageProducer implements MessageProducer<String> {

	private final int messageMaxLength;

	@Override
	public Message<String> produce() {
		return defaultProduce();
	}

	@Override
	public Supplier<String> getGenerator() {
		return () -> RandomStringUtils.secure().nextAlphabetic(messageMaxLength);
	}
}
