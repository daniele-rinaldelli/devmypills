package blog.devmypills;

import blog.devmypills.kickoff.regex.finder.JsonPathfinder;
import blog.devmypills.kickoff.regex.menu.ApplicationMenu;
import blog.devmypills.kickoff.regex.menu.ApplicationMenu.MenuEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static blog.devmypills.kickoff.regex.menu.ApplicationMenu.menuContent;
import static blog.devmypills.kickoff.regex.util.ConsolePrinter.printDefaultMainMenu;
import static blog.devmypills.kickoff.regex.util.ConsolePrinter.printOnNewLine;
import static blog.devmypills.kickoff.regex.util.ConsolePrinter.printUserAction;
import static blog.devmypills.kickoff.regex.util.ConsolePrinter.println;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static final Predicate<String[]> EXISTS_CONTEXT_PARAM = args -> args.length > 0 && args[0].contains("-Dcontext=");

	public static void main(String[] args) {
		LOGGER.info("Regex Kickoff");
		new App().execute(args);
	}

	private void execute(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {

			printDefaultMainMenu(menuContent());
			printOnNewLine("Please enter > ");

			MenuEntry menuEntry = getMenuEntryById(scanner);

			while (MenuEntry.EXIT != menuEntry) {
				try {
					switch (menuEntry) {
						case MenuEntry.FIND_PROPERTY -> {
							printUserAction("Input the property to find");
							String target = getTarget(scanner);
							printUserAction("Input the json file path", EXISTS_CONTEXT_PARAM.test(args));
							String context = getContext(scanner, args);

							var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();

							printOnNewLine("Paths:");
							printOnNewLine(jsonPathFinder.getPathsAsString());
						}
						default -> println("Input is not valid\n");
					}
				} catch (IllegalArgumentException ex) {
					printOnNewLine(ex.getMessage());
				}

				printOnNewLine(menuContent());
				printOnNewLine("Please enter > ");

				menuEntry = getMenuEntryById(scanner);
			}
			println("Goodbye");
		}
	}

	private MenuEntry getMenuEntryById(Scanner scanner) {
		return ApplicationMenu.getMenuEntryById(scanner.nextLine().trim());
	}

	private String getTarget(Scanner scanner) {
		String target = scanner.nextLine().trim();
		if (target.isEmpty()) throw new IllegalArgumentException("Empty property");
		if (!target.startsWith("\"") || !target.endsWith("\""))
			throw new IllegalArgumentException("Property must be a double quoted string");
		return target;
	}

	private static String getContext(Scanner scanner, String[] args) {

		if (EXISTS_CONTEXT_PARAM.test(args)) {
			return args[0].split("=")[1];
		}

		String path = scanner.nextLine().trim();
		try (var bufferedReader = new BufferedReader(new FileReader(path))) {
			return bufferedReader.lines().collect(Collectors.joining("\n"));
		} catch (Exception ex) {
			LOGGER.error("e", ex);
			throw new IllegalArgumentException("File not found");
		}
	}
}
