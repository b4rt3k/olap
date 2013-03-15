package pl.edu.pk.olap.realestate.core.parser;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import pl.edu.pk.olap.realestate.config.ConfigReader;
import pl.edu.pk.olap.realestate.config.ConfigurationConstants;

/**
 * @author b4rt3k
 * 
 */
public class ApartmentGuideParser implements HttpParser {

	@Autowired
	private ConfigReader reader;
	private int timeout;

	@Override
	public void parse() {
		timeout = Integer.parseInt(reader.getProperty(ConfigurationConstants.HTTP_CONNECTION_TIMEOUT_MILLIS));
		Document doc = null;
		try {
			doc = Jsoup.connect(reader.getProperty(ConfigurationConstants.APARTMENTGUIDE_URL)).timeout(timeout).userAgent("Mozilla").get();
			Elements links = doc.select("ul.browse_links>li>a[href]");
			for (Element link : links) {
				parseState(link.attr("abs:href"), link.text());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseState(String url, String stateName) {
		System.out.println("------------------------------------------");
		System.out.println(stateName);
		System.out.println("------------------------------------------");
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements links = doc.select(".padding_box ul>li>a[href]");
			for (Element link : links) {
				parseCity(link.attr("abs:href"), link.text());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseCity(String url, String cityName) {
		System.out.println(cityName);
		int pages = 1;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Element elem = doc.select("div.pagination>ol>li").last().select("a").first();
			pages = Integer.parseInt(new URL(elem.attr("abs:href")).getQuery().split("=")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < pages; i++) {
			parsePage(url + "?page=" + (i + 1));
		}
	}

	private void parsePage(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements items = doc.select("div.result");
			for (Element item : items) {
				System.out.println("	Name: " + item.select("div.column2>h3>a[href]").text());
				System.out.println("	Reigon: " + item.select("div.column2>ul>li.display_address>span[itemprop=addressRegion]").text());
				System.out.println("	Locality: " + item.select("div.column2>ul>li.display_address>span[itemprop=addressLocality]").text());
				System.out.println("	Postal Code: " + item.select("div.column2>ul>li.display_address>span[itemprop=postalCode]").text());
				System.out.println("	Phone Number: " + item.select("ul.listing_controls>li.phone_number.large.non_sem_number").text());
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseItem(String url) {

	}
}
