package blog.devmypills.kickoff.regex.formatter;

import java.util.List;
import java.util.stream.Collectors;

public class UnixPathFormatter implements PathFormatter {

	@Override
	public String showPath(List<List<String>> pathStructure) {
		return pathStructure.stream()
				.map(e -> e.stream().collect(Collectors.joining("/", "/", "")))
				.collect(Collectors.joining("\n"));
	}
}
