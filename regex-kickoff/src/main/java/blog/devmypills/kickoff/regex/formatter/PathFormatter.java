package blog.devmypills.kickoff.regex.formatter;

import java.util.List;
import java.util.Set;

public interface PathFormatter {

	String toString(List<List<String>> pathStructure);

	Set<String> toSet(List<List<String>> pathStructure);

}
