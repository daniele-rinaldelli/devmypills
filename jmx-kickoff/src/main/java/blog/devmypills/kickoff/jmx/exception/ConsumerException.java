package blog.devmypills.kickoff.jmx.exception;

public class ConsumerException extends RuntimeException {

	public ConsumerException() {
		super("Error consuming message");
	}
}
