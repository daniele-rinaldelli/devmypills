package blog.devmypills.kickoff.regex.finder;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonPathfinderTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathfinderTest.class);

	public record SimplePair(String target, Set<String> expectedResults) {
	}

	private static final Map<String, SimplePair> EXPECTATIONS_MAP = Map.of(
			"json-1.txt", new SimplePair("\"first\"", Set.of("/\"person\"/\"contact\"/\"mobile\"/\"first\" -> 1")),
			"json-2.txt", new SimplePair("\"model\"", Set.of("/\"person\"/\"cars\"/\"model\" -> 3")),
			"json-3.txt", new SimplePair(
					"\"model\"",
					Set.of(
							"/\"person\"/\"secondCar\"/\"model\" -> 1",
							"/\"person\"/\"thirdCar\"/\"model\" -> 1",
							"/\"person\"/\"firstCar\"/\"model\" -> 1"
					)
			)
	);

	@ParameterizedTest
	@MethodSource("provideJson")
	void findPath(String jsonContext, String testFileName) {
		LOGGER.warn("content: {}", jsonContext);
		String target = EXPECTATIONS_MAP.get(testFileName).target;

		var jsonPathFinder = JsonPathfinder.readyFor(target, jsonContext).findPath();

		Set<String> expectedResults = EXPECTATIONS_MAP.get(testFileName).expectedResults;
		assertEquals(expectedResults.size(), jsonPathFinder.getPathsAsSet().size());
		for (String currentExpectedResult : expectedResults) {
			assertTrue(jsonPathFinder.getPathsAsSet().contains(currentExpectedResult));
		}
	}

	private static Stream<Arguments> provideJson() {
		var arguments = new ArrayList<Arguments>();
		try {
			long testFilesNumber = Files.list(Path.of("src/test/resources")).count();
			for (int index = 0; index < testFilesNumber; index++) {
				String jsonFileName = "json-" + (index + 1) + ".txt";
				try (var bufferedReader = getBufferedReaderFor(jsonFileName)) {
					String jsonContent = bufferedReader.lines().collect(Collectors.joining("\n"));
					arguments.add(Arguments.argumentSet("file: " + jsonFileName, jsonContent, jsonFileName));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("e", ex);
		}

		return arguments.stream();
	}

	private static BufferedReader getBufferedReaderFor(String fileName) {
		return new BufferedReader(
				new InputStreamReader(
						JsonPathfinderTest.class.getClassLoader().getResourceAsStream(fileName)
				)
		);
	}
}