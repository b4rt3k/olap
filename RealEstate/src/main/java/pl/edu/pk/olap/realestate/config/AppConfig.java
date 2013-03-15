package pl.edu.pk.olap.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.edu.pk.olap.realestate.core.parser.ApartmentGuideParser;

/**
 * @author b4rt3k
 * 
 */
@Configuration
public class AppConfig {

	@Bean
	public ConfigReader configReader() {
		return new ConfigReader();
	}

	@Bean
	public ApartmentGuideParser apartmentGuideParser() {
		return new ApartmentGuideParser();
	}
}
