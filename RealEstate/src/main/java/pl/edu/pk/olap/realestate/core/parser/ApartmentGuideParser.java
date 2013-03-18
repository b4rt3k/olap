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
				System.out.println("--Name: " + item.select("div.column2>h3>a[href]").text());
				System.out.println("--Reigon: " + item.select("div.column2>ul>li.display_address>span[itemprop=addressRegion]").text());
				System.out.println("--Locality: " + item.select("div.column2>ul>li.display_address>span[itemprop=addressLocality]").text());
				System.out.println("--Postal Code: " + item.select("div.column2>ul>li.display_address>span[itemprop=postalCode]").text());
				System.out.println("--Phone Number: " + item.select("ul.listing_controls>li.phone_number.large.non_sem_number").text());
				parseItem(item.select("div.column2>h3>a[href]").attr("abs:href"));
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseItem(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements elems = doc.select("div#floorplans_and_pricing_tab table.unit_detail tr").last().select("td");
			System.out.println("--Style: " + elems.eq(0).text());
			System.out.println("--Beds: " + elems.eq(1).text());
			System.out.println("--Bathrooms: " + elems.eq(2).text());
			System.out.println("--Ares [Sq. Ft.]: " + elems.eq(4).text());
			System.out.println("--Price: " + elems.eq(5).text());
			System.out.println("--Term: " + elems.eq(6).text());
			System.out.println("--Deposit: " + elems.eq(7).text());
			Element description = doc.select("div#details_description #listing_description").first();
			System.out.println("--Description: " + description.text());
			System.out.println("--Features: ");
			Elements features = doc.select("div#apartment_features_tab ul.features>li");
			for (Element feature : features) {
				System.out.println("----" + feature.text());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
