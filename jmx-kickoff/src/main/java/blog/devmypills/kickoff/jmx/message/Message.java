package blog.devmypills.kickoff.jmx.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
@ToString
public class Message<T> {

	private final T data;
	private final Instant instant = Instant.now();
}
