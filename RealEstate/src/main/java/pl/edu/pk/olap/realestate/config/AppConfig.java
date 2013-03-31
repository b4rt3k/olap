package pl.edu.pk.olap.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.edu.pk.olap.realestate.core.parser.ApartmentGuideParser;
import pl.edu.pk.olap.realestate.core.parser.RealtorParser;
import pl.edu.pk.olap.realestate.core.parser.RentalsParser;

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

	@Bean
	public RentalsParser rentalsParser() {
		return new RentalsParser();
	}

	@Bean
	public RealtorParser realtorParser() {
		return new RealtorParser();
	}
}
