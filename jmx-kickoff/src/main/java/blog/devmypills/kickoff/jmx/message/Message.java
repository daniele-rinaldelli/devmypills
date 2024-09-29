package blog.devmypills.kickoff.jmx.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
public class Message<T> {

	private final T data;
	private final Instant instant = Instant.now();

	@Override
	public String toString() {
		return "Message{" +
				"data=" + data +
				", instant=" + instant +
				'}';
	}
}
