package pl.edu.pk.olap.realestate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import pl.edu.pk.olap.realestate.config.AppConfig;
import pl.edu.pk.olap.realestate.config.AppContext;
import pl.edu.pk.olap.realestate.core.parser.HttpParser;
import pl.edu.pk.olap.realestate.core.parser.TruliaParser;

public class TestClass {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext cntxt = new AnnotationConfigApplicationContext(AppConfig.class, AppContext.class);
		final HttpParser parser = AppContext.get(TruliaParser.class);
		new Thread(new Runnable() {

			@Override
			public void run() {
				parser.parse();
			}
		}).start();

	}
}
