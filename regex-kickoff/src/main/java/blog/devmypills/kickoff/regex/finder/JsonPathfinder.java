package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonPathfinder implements Pathfinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathfinder.class);

	private static final String TARGET_PLACEHOLDER = "<target>";
	private static final String TARGET_GROUP_NAME = "targetGroupName";
	private static final String INTERNAL_TARGET_PLACEHOLDER = "<~._.~>";

	private static final String REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS = "(\\{[^{}]*})(?=.*\"" + TARGET_PLACEHOLDER + "\":)";
	private static final String REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS = "(\"(?<" + TARGET_GROUP_NAME + ">[^\"]+)\")(?=:\\{.*\"" + TARGET_PLACEHOLDER + "\")";

	private static final String KEY_USELESS_CONTAINER_FOR_TARGET = "keyUselessContainerForTarget";
	private static final String KEY_CONTAINER_FOR_TARGET = "keyContainerForTarget";
	private static final String KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET = "keyUselessContainerForInternalTarget";
	private static final String KEY_CONTAINER_FOR_INTERNAL_TARGET = "keyContainerForInternalTarget";
	private static final String KEY_TARGET = "keyTarget";
	private static final String KEY_INTERNAL_TARGET = "keyInternalTarget";

	private List<List<String>> resultPath;

	private final Map<String, Pattern> patterns = new HashMap<>();

	private final String initialTarget;
	private final String initialContext;

	private JsonPathfinder(String target, String context) {
		initialTarget = target;
		initialContext = context;

		patterns.put(KEY_USELESS_CONTAINER_FOR_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS.replace(TARGET_PLACEHOLDER, initialTarget)));
		patterns.put(KEY_CONTAINER_FOR_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS.replace(TARGET_PLACEHOLDER, initialTarget)));
		patterns.put(KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS.replace(TARGET_PLACEHOLDER, INTERNAL_TARGET_PLACEHOLDER)));
		patterns.put(KEY_CONTAINER_FOR_INTERNAL_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS.replace(TARGET_PLACEHOLDER, INTERNAL_TARGET_PLACEHOLDER)));
		patterns.put(KEY_TARGET, Pattern.compile(initialTarget));
		patterns.put(KEY_INTERNAL_TARGET, Pattern.compile(INTERNAL_TARGET_PLACEHOLDER));
	}

	public static JsonPathfinder readyFor(String target, String context) {
		return new JsonPathfinder(target, context);
	}

	@Override
	public Pathfinder findPath() {
		String adjustedContext = adjustContext(initialContext);
		resultPath = executeFindMultiMatch(initialTarget, adjustedContext);

		LOGGER.info("Result: {}", resultPath);
		return this;
	}

	@Override
	public boolean isPathFound() {
		return !resultPath.isEmpty();
	}

	@Override
	public Set<String> getPaths(PathFormatter pathFormatter) {
		return pathFormatter.toSet(resultPath);
	}

	public String getPathsAsString(PathFormatter pathFormatter) {
		return pathFormatter.toString(resultPath);
	}

	private String adjustContext(String context) {
		return context.replace("[", "{")
				.replace("]", "}")
				.replaceAll("\\s", "")
				.replaceAll("\\t", "");
	}

	private List<List<String>> executeFindMultiMatch(String target, String context) {

		List<List<String>> result = new ArrayList<>();
		String targetReplacement = INTERNAL_TARGET_PLACEHOLDER;
		String replacedContext = context;

		int matchNumber = 0;
		while ((matchNumber = countMatchNumber(target, replacedContext)) > 1) {
			replacedContext = replaceTargets(target, replacedContext, targetReplacement, matchNumber - 1);
			result.add(executeFindSingleMatch(target, replacedContext));
			replacedContext = replacedContext.replace(target, "");
			String tempTarget = target;
			target = targetReplacement;
			targetReplacement = tempTarget;
		}
		if (matchNumber == 1) {
			result.add(executeFindSingleMatch(target, replacedContext));
		}

		return result;
	}

	private String replaceTargets(String target, String context, String replacement, int replacementNumber) {

		Pattern pattern = getPattern(target, KEY_TARGET, KEY_INTERNAL_TARGET);
		Matcher matcher = pattern.matcher(context);

		while (matcher.find() && replacementNumber > 0) {
			context = matcher.replaceFirst(replacement);
			matcher = pattern.matcher(context);
			replacementNumber--;
		}

		return context;
	}

	private int countMatchNumber(String target, String context) {

		Pattern pattern = getPattern(target, KEY_TARGET, KEY_INTERNAL_TARGET);

		Matcher matcher = pattern.matcher(context);
		int counter = 0;
		while (matcher.find()) {
			counter++;
		}
		return counter;
	}

	private List<String> executeFindSingleMatch(String target, String context) {
		List<String> containers = new ArrayList<>();

		Pattern patternForUselessContainerObjects = getPattern(target, KEY_USELESS_CONTAINER_FOR_TARGET, KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET);
		Matcher matcher = patternForUselessContainerObjects.matcher(context);

		while (matcher.find()) {
			context = matcher.replaceAll("");
			matcher = patternForUselessContainerObjects.matcher(context);
		}

		Pattern patternForContainersObjects = getPattern(target, KEY_CONTAINER_FOR_TARGET, KEY_CONTAINER_FOR_INTERNAL_TARGET);
		Matcher matcherContainerObjects = patternForContainersObjects.matcher(context);

		while (matcherContainerObjects.find()) {
			String group = matcherContainerObjects.group(TARGET_GROUP_NAME);
			containers.add(group);
		}

		if (!containers.isEmpty()) {
			containers.add(target.equals(INTERNAL_TARGET_PLACEHOLDER) ? this.initialTarget : target);
		}

		LOGGER.info("Containers: {}", containers);
		return containers;
	}

	private Pattern getPattern(String target, String keyTarget, String keyInternalTarget) {
		return switch (target) {
			case String t when t.equals(initialTarget) -> patterns.get(keyTarget);
			case String t when t.equals(INTERNAL_TARGET_PLACEHOLDER) -> patterns.get(keyInternalTarget);
			case null, default -> throw new RuntimeException("No suitable patter available");
		};
	}
}