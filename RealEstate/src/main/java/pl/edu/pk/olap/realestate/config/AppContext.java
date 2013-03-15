package pl.edu.pk.olap.realestate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author b4rt3k
 * 
 */
@Component
public class AppContext {

	private static ApplicationContext applicationContext;

	@Autowired
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		AppContext.applicationContext = applicationContext;
	}

	public static <T> T get(final Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
}
