package blog.devmypills.kickoff.jmx.coordinator;

public interface CoordinatorMXBean {

	int countMessages();

	void runProducer();

	void runConsumer();

	void stopProducer();

	void stopConsumer();
}
