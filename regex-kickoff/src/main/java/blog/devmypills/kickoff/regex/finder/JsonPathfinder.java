package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonPathfinder implements Pathfinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathfinder.class);

	private static final String TARGET_PLACEHOLDER = "<target>";
	private static final String TARGET_GROUP_NAME = "targetGroupName";
	private static final String INTERNAL_TARGET_REPLACEMENT_FACE = "<~._.~>";

	private String regexForUselessContainerObjects = "(\\{[^{}]*})(?=.*\"" + TARGET_PLACEHOLDER + "\":)";
	private String regexForContainersObjects = "(\"(?<" + TARGET_GROUP_NAME + ">[^\"]+)\")(?=:\\{.*\"" + TARGET_PLACEHOLDER + "\")";

	private final List<List<String>> resultPath = new ArrayList<>();

	private final String initialTarget;
	private final String initialContext;

	//TODO: optimize regex compilation
	private JsonPathfinder(String target, String context) {
		this.initialTarget = target;
		this.initialContext = context;
	}

	public static JsonPathfinder readyFor(String target, String context) {
		return new JsonPathfinder(target, context);
	}

	@Override
	public Pathfinder findPath() {
		regexForUselessContainerObjects = regexForUselessContainerObjects.replace(TARGET_PLACEHOLDER, initialTarget);
		regexForContainersObjects = regexForContainersObjects.replace(TARGET_PLACEHOLDER, initialTarget);

		executeFindInMultiTargetContext(initialTarget, initialContext, resultPath);

		LOGGER.info("Result: {}", resultPath);
		return this;
	}

	@Override
	public boolean isPathFound() {
		return !resultPath.isEmpty();
	}

	@Override
	public String getFormattedPath(PathFormatter pathFormatter) {
		return pathFormatter.showPath(resultPath);
	}

	private boolean isMultiTargetContext(String target, String context) {
		return countMatchNumber(target, context) > 1;
	}

	private boolean isSingleTargetContext(String target, String context) {
		return countMatchNumber(target, context) == 1;
	}

	private String replaceTargets(String target, String context, String replacement, int replacementNumber) {
		Pattern pattern = Pattern.compile(target);
		Matcher matcher = pattern.matcher(context);

		while (matcher.find() && replacementNumber > 0) {
			context = matcher.replaceFirst(replacement);
			matcher = pattern.matcher(context);
			replacementNumber--;
		}

		return context;
	}

	private int countMatchNumber(String target, String context) {
		Pattern pattern = Pattern.compile(target);
		Matcher matcher = pattern.matcher(context);
		int counter = 0;
		while (matcher.find()) {
			counter++;
		}
		return counter;
	}

	private void executeFindInMultiTargetContext(String target, String context, List<List<String>> globalResult) {

		String targetReplacement = INTERNAL_TARGET_REPLACEMENT_FACE;
		String replacedContext = context;

		//TODO: remove double count call, get count number directly
		while (isMultiTargetContext(target, replacedContext)) {
			int matchNumber = countMatchNumber(target, replacedContext);
			replacedContext = replaceTargets(target, replacedContext, targetReplacement, matchNumber - 1);
			globalResult.add(executeFind(target, replacedContext));
			replacedContext = replacedContext.replace(target, "");
			regexForUselessContainerObjects = regexForUselessContainerObjects.replace(target, targetReplacement);
			regexForContainersObjects = regexForContainersObjects.replace(target, targetReplacement);
			String tempTarget = target;
			target = targetReplacement;
			targetReplacement = tempTarget;
		}
		if (isSingleTargetContext(target, replacedContext)) {
			globalResult.add(executeFind(target, replacedContext));
		}
	}

	private List<String> executeFind(String target, String context) {
		List<String> containers = new ArrayList<>();

		Pattern patternForUselessContainerObjects = Pattern.compile(regexForUselessContainerObjects);
		Matcher matcher = patternForUselessContainerObjects.matcher(context);

		while (matcher.find()) {
			context = matcher.replaceAll("");
			matcher = patternForUselessContainerObjects.matcher(context);
		}

		Pattern patternForContainersObjects = Pattern.compile(regexForContainersObjects);
		Matcher matcherContainerObjects = patternForContainersObjects.matcher(context);

		while (matcherContainerObjects.find()) {
			String group = matcherContainerObjects.group(TARGET_GROUP_NAME);
			containers.add(group);
		}

		if (!containers.isEmpty()) {
			containers.add(target.equals(INTERNAL_TARGET_REPLACEMENT_FACE) ? this.initialTarget : target);
		}

		LOGGER.info("Containers: {}", containers);
		return containers;
	}
}