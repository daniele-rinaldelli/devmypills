package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import blog.devmypills.kickoff.regex.formatter.UnixPathFormatter;

import java.util.Set;

public interface Pathfinder {

	Pathfinder findPath();

	boolean isPathFound();

	Set<String> getPathsAsSet(PathFormatter pathFormatter);

	String getPathsAsString(PathFormatter pathFormatter);

	default Set<String> getPathsAsSet() {
		return isPathFound() ? getPathsAsSet(new UnixPathFormatter()) : Set.of();
	}

	default String getPathsAsString() {
		return isPathFound() ? getPathsAsString(new UnixPathFormatter()) : "No paths found";
	}
}
