package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import lombok.Getter;
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

	private static final String REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS = "(\\{(?![^{}]*?" + TARGET_PLACEHOLDER + ":)[^{}]*?})";
	private static final String REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS = "(?<" + TARGET_GROUP_NAME + ">\"[^\"]+\")(?=:\\{.*" + TARGET_PLACEHOLDER + ")";

	private static final String KEY_USELESS_CONTAINER_FOR_TARGET = "keyUselessContainerForTarget";
	private static final String KEY_CONTAINER_FOR_TARGET = "keyContainerForTarget";
	private static final String KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET = "keyUselessContainerForInternalTarget";
	private static final String KEY_CONTAINER_FOR_INTERNAL_TARGET = "keyContainerForInternalTarget";
	private static final String KEY_TARGET = "keyTarget";
	private static final String KEY_INTERNAL_TARGET = "keyInternalTarget";

	private List<List<String>> resultPath;

	private final Map<String, Pattern> patterns = new HashMap<>();

	@Getter
	private final String initialTarget;
	@Getter
	private final String initialContext;

	private final String adjustedTarget;
	private final String adjustedContext;

	private JsonPathfinder(String target, String context) {
		initialTarget = target;
		initialContext = context;

		adjustedTarget = adjustTarget(target);
		adjustedContext = adjustContext(initialContext);

		patterns.put(KEY_USELESS_CONTAINER_FOR_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS.replace(TARGET_PLACEHOLDER, adjustedTarget)));
		patterns.put(KEY_CONTAINER_FOR_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS.replace(TARGET_PLACEHOLDER, adjustedTarget)));
		patterns.put(KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_USELESS_CONTAINER_OBJECTS.replace(TARGET_PLACEHOLDER, INTERNAL_TARGET_PLACEHOLDER)));
		patterns.put(KEY_CONTAINER_FOR_INTERNAL_TARGET, Pattern.compile(REGEX_TEMPLATE_FOR_CONTAINERS_OBJECTS.replace(TARGET_PLACEHOLDER, INTERNAL_TARGET_PLACEHOLDER)));
		patterns.put(KEY_TARGET, Pattern.compile(adjustedTarget));
		patterns.put(KEY_INTERNAL_TARGET, Pattern.compile(INTERNAL_TARGET_PLACEHOLDER));
	}

	public static JsonPathfinder readyFor(String target, String context) {
		return new JsonPathfinder(target, context);
	}

	@Override
	public Pathfinder findPath() {
		resultPath = executeFindMultiMatch(adjustedTarget, adjustedContext);

		LOGGER.debug("Result: {}", resultPath);
		return this;
	}

	@Override
	public boolean isPathFound() {
		return !resultPath.isEmpty();
	}

	@Override
	public Set<String> getPathsAsSet(PathFormatter pathFormatter) {
		return pathFormatter.toSet(resultPath);
	}

	@Override
	public String getPathsAsString(PathFormatter pathFormatter) {
		return pathFormatter.toString(resultPath);
	}

	private String adjustTarget(String target) {
		return target.replaceAll("\\s+", "");
	}

	private String adjustContext(String context) {
		var adjustedContextBuilder = new StringBuilder();
		String minificationRegex = "(\\s+)|(\\[)|(])";
		Matcher matcher = Pattern.compile(minificationRegex).matcher(context);
		while (matcher.find()) {
			if (matcher.group(1) != null) {
				matcher.appendReplacement(adjustedContextBuilder, "");
			}
			if (matcher.group(2) != null) {
				matcher.appendReplacement(adjustedContextBuilder, "{");
			}
			if (matcher.group(3) != null) {
				matcher.appendReplacement(adjustedContextBuilder, "}");
			}
		}
		matcher.appendTail(adjustedContextBuilder);
		LOGGER.debug("adjusted context: {}", adjustedContextBuilder);
		return adjustedContextBuilder.toString();
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
			matcher.reset(context);
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

		Pattern patternForUselessContainerObject = getPattern(target, KEY_USELESS_CONTAINER_FOR_TARGET, KEY_USELESS_CONTAINER_FOR_INTERNAL_TARGET);
		Matcher matcher = patternForUselessContainerObject.matcher(context);

		while (matcher.find()) {
			context = matcher.replaceAll("");
			matcher.reset(context);
		}

		Pattern patternForContainersObject = getPattern(target, KEY_CONTAINER_FOR_TARGET, KEY_CONTAINER_FOR_INTERNAL_TARGET);
		Matcher matcherContainerObjects = patternForContainersObject.matcher(context);

		while (matcherContainerObjects.find()) {
			String group = matcherContainerObjects.group(TARGET_GROUP_NAME);
			containers.add(group);
		}

		containers.add(initialTarget);

		LOGGER.debug("Containers: {}", containers);
		return containers;
	}

	private Pattern getPattern(String target, String keyTarget, String keyInternalTarget) {
		return switch (target) {
			case String t when t.equals(adjustedTarget) -> patterns.get(keyTarget);
			case String t when t.equals(INTERNAL_TARGET_PLACEHOLDER) -> patterns.get(keyInternalTarget);
			case null, default -> throw new RuntimeException("No suitable patter available");
		};
	}
}