package blog.devmypills.kickoff.regex.finder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonPathfinderTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathfinderTest.class);

	@Test
	void findPath() {
		String context = """
				{"person":{"full name":"Dana Waters","address":{"street":"47556 Gina Dale","city":"Chaeryŏng-ŭp"},"contact":{"mobile":{"first":"06 12345678","second":"06 963852"},"home":"333 6584147","email":"fake@mail.com"}}}
				""";
		String target = "first";

		var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();
		String formattedPath = jsonPathFinder.getFormattedPath();

		assertEquals("/person/contact/mobile/first", formattedPath);
	}

	@Test
	@Disabled("Not yet implemented")
	void findPathWithinArray() {
		String context = """
				{"person":{"full name":"Marco Kassulke","address":{"street":"6976 Ward Ranch","city":"Laayoune"},"cars":[{"brand":"lamborghini","model":"miura","year":"2009"},{"brand":"ferrari","model":"california","year":"2014"},{"brand":"maserati","model":"ghibli","year":"2015"}]}}
				""";

		String target = "model";
		var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();
		String formattedPath = jsonPathFinder.getFormattedPath();
		LOGGER.info("Formatted path: {}", formattedPath);
	}

	@Test
	void findInMultiTargetContext() {
		String context = """
				{"person":{"full name":"Marco Kassulke","address":{"street":"6976 Ward Ranch","city":"Laayoune"},"firstCar":{"brand":"lamborghini","model":"miura","year":"2009"},"secondCar":{"brand":"ferrari","model":"california","year":"2014"},"thirdCar":{"brand":"maserati","model":"ghibli","year":"2015"}}}
				""";
		String target = "model";

		Pathfinder pathFinder = JsonPathfinder.readyFor(target, context).findPath();

		String expected = """
				/person/thirdCar/model
				/person/secondCar/model
				/person/firstCar/model
				""";
		assertEquals(expected.trim(), pathFinder.getFormattedPath().trim());
	}
}