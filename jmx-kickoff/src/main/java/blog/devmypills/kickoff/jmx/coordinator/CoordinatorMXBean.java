package blog.devmypills.kickoff.jmx.coordinator;

public interface CoordinatorMXBean {

	int countMessages();

	int countRunningProducers();

	void runProducer();

	void stopProducer();

	int countRunningConsumers();

	void runConsumer();

	void stopConsumer();
}
