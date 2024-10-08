package blog.devmypills.kickoff.jmx.annotation;

import javax.management.DescriptorKey;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JmxOperation {

	@DescriptorKey("Description")
	String description();
}
