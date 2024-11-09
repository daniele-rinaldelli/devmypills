package blog.devmypills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		LOGGER.info("Regex Kickoff");
		//TODO: implement main REPL
		new App().execute("target", "context");
	}

	private void execute(String target, String context) {
		LOGGER.info("Path for {} in {}", target, context);
	}
}
