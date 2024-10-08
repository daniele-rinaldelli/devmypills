package blog.devmypills.kickoff.jmx.coordinator;

import blog.devmypills.kickoff.jmx.annotation.JmxOperation;

public interface QueueCoordinatorMBean {

	@JmxOperation(description = "Retrieve the current number of messages inside the queue")
	int countMessages();

	@JmxOperation(description = "Retrieve the current number of running producers")
	int countRunningProducers();

	@JmxOperation(description = "Spin a new producer thread")
	void runProducer();

	@JmxOperation(description = "Stop a running producer thread")
	void stopProducer();

	@JmxOperation(description = "Retrieve the current number of running consumers")
	int countRunningConsumers();

	@JmxOperation(description = "Spin a new consumer thread")
	void runConsumer();

	@JmxOperation(description = "Stop a running consumer thread")
	void stopConsumer();
}
