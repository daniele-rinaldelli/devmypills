package blog.devmypills.kickoff.regex.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsolePrinter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);

	private ConsolePrinter() {
	}

	public static void printUserAction(String command) {
		LOGGER.info("\n{} {}", command, " > ");
	}

	public static void printOnNewLine(String param) {
		LOGGER.info("\n{}", param);
	}

	public static void println(String param) {
		LOGGER.info("\n{} \n", param);
	}

	public static void printDefaultMainMenu(String menu) {
		LOGGER.info("\n{}", menu);
	}
}
