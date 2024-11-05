package blog.devmypills.kickoff.regex.formatter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnixPathFormatter implements PathFormatter {

	@Override
	public String showPath(List<List<String>> pathStructure) {
		return pathStructure.stream()
				.map(pathElements -> pathElements.stream().collect(Collectors.joining("/", "/", "")))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.map(pathEntry -> pathEntry.getKey() + " -> " + pathEntry.getValue())
				.collect(Collectors.joining("\n"));
	}
}
