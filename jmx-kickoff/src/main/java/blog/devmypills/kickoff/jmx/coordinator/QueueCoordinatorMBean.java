package blog.devmypills.kickoff.jmx.coordinator;

import blog.devmypills.kickoff.jmx.annotation.JmxOperation;

public interface QueueCoordinatorMBean {

	@JmxOperation(name = "Retrieve the current number of messages inside the queue")
	int countMessages();

	@JmxOperation(name = "Retrieve the current number of running producers")
	int countRunningProducers();

	@JmxOperation(name = "Spin a new producer thread")
	void runProducer();

	@JmxOperation(name = "Stop a running producer thread")
	void stopProducer();

	@JmxOperation(name = "Retrieve the current number of running consumers")
	int countRunningConsumers();

	@JmxOperation(name = "Spin a new consumer thread")
	void runConsumer();

	@JmxOperation(name = "Stop a running consumer thread")
	void stopConsumer();
}
