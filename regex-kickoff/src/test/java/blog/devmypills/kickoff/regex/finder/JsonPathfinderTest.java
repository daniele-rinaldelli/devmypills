package blog.devmypills.kickoff.regex.finder;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonPathfinderTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathfinderTest.class);

	//TODO: params test

	@Test
	void findPath() {
		String context = """
				{"person":{"full name":"Dana Waters","address":{"street":"47556 Gina Dale","city":"Chaeryŏng-ŭp"},"contact":{"mobile":{"first":"06 12345678","second":"06 963852"},"home":"333 6584147","email":"fake@mail.com"}}}
				""";
		String target = "first";

		var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();

		String formattedPath = jsonPathFinder.getFormattedPath();
		LOGGER.debug(formattedPath);
		assertEquals("/person/contact/mobile/first -> 1", formattedPath);
	}

	@Test
	void findPathWithinArray() {
		String context = """
				{"person":{"full name":"Marco Kassulke","address":{"street":"6976 Ward Ranch","city":"Laayoune"},"cars":[{"brand":"lamborghini","model":"miura","year":"2009"},{"brand":"ferrari","model":"california","year":"2014"},{"brand":"maserati","model":"ghibli","year":"2015"}]}}
				""";

		String target = "model";
		var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();

		String formattedPath = jsonPathFinder.getFormattedPath();
		LOGGER.debug(formattedPath);
		assertTrue(formattedPath.contains("/person/cars/model -> 3"));
	}

	@Test
	void findInMultiTargetContext() {
		String context = """
				{"person":{"full name":"Marco Kassulke","address":{"street":"6976 Ward Ranch","city":"Laayoune"},"firstCar":{"brand":"lamborghini","model":"miura","year":"2009"},"secondCar":{"brand":"ferrari","model":"california","year":"2014"},"thirdCar":{"brand":"maserati","model":"ghibli","year":"2015"}}}
				""";
		String target = "model";

		var jsonPathFinder = JsonPathfinder.readyFor(target, context).findPath();

		String formattedPath = jsonPathFinder.getFormattedPath();
		LOGGER.debug(formattedPath);
		assertTrue(formattedPath.contains("/person/secondCar/model -> 1"));
		assertTrue(formattedPath.contains("/person/thirdCar/model -> 1"));
		assertTrue(formattedPath.contains("/person/firstCar/model -> 1"));
	}
}