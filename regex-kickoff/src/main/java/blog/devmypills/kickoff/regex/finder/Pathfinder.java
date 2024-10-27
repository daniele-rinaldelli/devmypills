package blog.devmypills.kickoff.regex.finder;

import blog.devmypills.kickoff.regex.formatter.PathFormatter;
import blog.devmypills.kickoff.regex.formatter.UnixPathFormatter;

public interface Pathfinder {

	Pathfinder findPath();

	boolean isPathFound();

	String getFormattedPath(PathFormatter pathFormatter);

	default String getFormattedPath() {
		return isPathFound() ? getFormattedPath(new UnixPathFormatter()) : "";
	}
}
