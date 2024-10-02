package blog.devmypills.kickoff.jmx.exception;

public class ProducerException extends RuntimeException {

	public ProducerException() {
		super("Error producing message");
	}
}
