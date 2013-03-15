package pl.edu.pk.olap.realestate.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author b4rt3k
 * 
 */
public class ConfigReader extends Properties {
	private static final long serialVersionUID = 270433101005850311L;

	public ConfigReader() {
		InputStream is;
		try {
			is = new FileInputStream(System.getProperty("config"));
			this.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
