package pl.edu.pk.olap.realestate.config;

import java.io.InputStream;

/**
 * @author b4rt3k
 * 
 */
public class ResourcesReader {
	public static InputStream getResourceByName(String name) {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(name);
	}
}
