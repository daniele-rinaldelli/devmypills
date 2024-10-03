package blog.devmypills.kickoff.jmx.exception;

public class ProducerInterruptedException extends RuntimeException {

	public ProducerInterruptedException() {
		super("Error producing message");
	}
}
