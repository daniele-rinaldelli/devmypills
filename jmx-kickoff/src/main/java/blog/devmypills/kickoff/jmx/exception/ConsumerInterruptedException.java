package blog.devmypills.kickoff.jmx.exception;

public class ConsumerInterruptedException extends RuntimeException {

	public ConsumerInterruptedException() {
		super("Error consuming message");
	}
}
