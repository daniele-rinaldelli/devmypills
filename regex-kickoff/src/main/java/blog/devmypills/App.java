package blog.devmypills;

import blog.devmypills.kickoff.regex.finder.JsonPathfinder;
import blog.devmypills.kickoff.regex.formatter.UnixPathFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		LOGGER.info("Regex Kickoff");
		String context = """
				{"person":{"full name":"Dana Waters","address":{"street":"47556 Gina Dale","city":"Chaeryŏng-ŭp"},"contact":{"home":{"first":"06 12345678","second":"06 963852"},"mobile":"333 6584147","email":"fake@mail.com"}}}
				""";
		String target = "first";
		new App().execute(target, context);
	}

	private void execute(String target, String context) {

		String formattedPath = JsonPathfinder.readyFor(target, context)
				.findPath()
				.getFormattedPath(new UnixPathFormatter());

		LOGGER.info("Path: {}", formattedPath);
	}
}
