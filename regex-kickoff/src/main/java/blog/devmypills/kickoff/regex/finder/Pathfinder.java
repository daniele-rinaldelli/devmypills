package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import blog.devmypills.kickoff.regex.formatter.UnixPathFormatter;

import java.util.Set;

public interface Pathfinder {

	Pathfinder findPath();

	boolean isPathFound();

	Set<String> getPaths(PathFormatter pathFormatter);

	default Set<String> getPaths() {
		return isPathFound() ? getPaths(new UnixPathFormatter()) : Set.of();
	}
}
