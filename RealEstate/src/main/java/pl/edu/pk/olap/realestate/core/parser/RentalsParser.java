package pl.edu.pk.olap.realestate.core.parser;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import pl.edu.pk.olap.realestate.config.ConfigReader;
import pl.edu.pk.olap.realestate.config.ConfigurationConstants;

// jeœli wszystkie parsery bêd¹ mia³y podobn¹ strukturê, to zrobiæ klasê abstrakcyjn¹, a w podklasach podmieniaæ tylko selektory!!!
// trzeba te¿ zrobiæ mapê skrótów nazw stanów na nazwy pe³ne
// w parsePage i parseItem zprawdzac wszystkie selektory czy znajduj¹ odpowiednie elementy na stronie
// ujednoliciæ wielkoœæ znakó (tolowercase)
// regu³y dla price i deposit zrobiæ w metodzie (klasy abstract parser albo xxxUtils, bo liczbowy format trzeba ujednoliciæ NumberFormat)
// wszystko trimowaæ
// problem z dolarem przed cen¹ i np 400 i 400.00 (wszystkie liczby maj¹ problem z kropk¹) oraz np $400-$600, czyli pauza
/**
 * 
 * @author b4rt3k
 * 
 */
public class RentalsParser implements HttpParser {

	@Autowired
	private ConfigReader reader;
	private int timeout;
	private static Logger log = Logger.getLogger(RentalsParser.class);

	@Override
	public void parse() {
		log.info("Rentals parsing started");
		timeout = Integer.parseInt(reader.getProperty(ConfigurationConstants.HTTP_CONNECTION_TIMEOUT_MILLIS));
		Document doc = null;
		try {
			doc = Jsoup.connect(reader.getProperty(ConfigurationConstants.RENTALS_URL)).timeout(timeout).userAgent("Mozilla").get();
			Elements links = doc.select("div#search_locations_popup ul.search_locations_states>li>a[href]");
			if (links.size() > 0) {
				log.info("States links found.");
				for (Element link : links) {
					parseState(link.attr("abs:href"), link.text());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void parseState(String url, String stateName) {
		log.info("Parsing state: " + stateName);
		System.out.println("------------------------------------------");
		System.out.println(stateName);
		System.out.println("------------------------------------------");
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements links = doc.select("div.content div#state_links>ul>li>a[href]");
			if (links.size() > 0) {
				log.info("Cities links found.");
				for (Element link : links) {
					parseCity(link.attr("abs:href"), link.text());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	private void parseCity(String url, String cityName) {
		log.info("Parsing city: " + cityName);
		int pages = 1;
		boolean resultsFound = false;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Element elem = doc.select("div#search_results_container div.pagination>h2").first();
			if (elem != null) {
				resultsFound = true;
				String text = elem.text().trim();
				pages = Integer.parseInt(text.substring(text.lastIndexOf(" ") + 1, text.length()));
				log.info("Pages count found: " + pages);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}

		if (resultsFound) {
			for (int i = 0; i < pages; i++) {
				parsePage(url + "?page=" + (i + 1), cityName, (i + 1));
			}
		}
	}

	private void parsePage(String url, String city, int page) {
		log.info("Parsing page: " + page + ", city: " + city);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements items = doc.select("div#search_results_container div#search_results div.result");
			int index = 0;
			if (items.size() > 0) {
				log.info("Apartments base info found.");
				for (Element item : items) {
					System.out.println("--Name: " + item.select("div.listing_details .listing_name>a[href]").text());
					String[] location = item.select("div.listing_details .listing_location").text().split(",");
					String[] subLoc = location[1].trim().split(" ");
					System.out.println("--Reigon: " + subLoc[0]);
					System.out.println("--Locality: " + location[0]);
					System.out.println("--Postal Code: " + subLoc[1]);
					System.out.println("--Phone Number: " + item.select("div.listing_details .listing_phone").text());
					parseItem(item.select("div.listing_details .listing_name>a[href]").attr("abs:href"), index++);
					System.out.println();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void parseItem(String url, int index) {
		log.info("Parsing item: " + index);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Element street = doc.select("div.content #summary_address").first();
			System.out.println("--Street: " + street.text().split("\\|")[0].trim());
			Element lastRow = doc.select("table.floorPlanTable tr").last();
			if (lastRow != null) {
				// details may be in table
				log.info("Apartment details found (floorplan table).");
				Elements elems = lastRow.select("td");
				System.out.println("--Style: " + elems.eq(0).text());
				System.out.println("--Beds: " + elems.eq(1).text());
				System.out.println("--Bathrooms: " + elems.eq(2).text());
				System.out.println("--Area [Sq. Ft.]: " + elems.eq(4).text().replaceAll("\\D", ""));
				System.out.println("--Price: " + elems.eq(5).text().replaceAll("[^\\$|\\.|\\d|-]", ""));
				System.out.println("--Term: " + elems.eq(6).text());
				System.out.println("--Deposit: " + elems.eq(7).text().replaceAll("[^\\$|\\.|\\d]", "").trim());
			} else {
				// or not
				Element floorplan = doc.select("div.content #summary_floorplan").first();
				System.out.println("--Style: ");
				if (floorplan != null) {
					log.info("Apartment details found (floorplan).");
					String[] tab = floorplan.text().split("\\|");
					int size = tab.length;
					System.out.println("--Beds: " + (size >= 1 ? tab[0].replaceAll("\\D", "") : ""));
					System.out.println("--Bathrooms: " + (size >= 2 ? tab[1].replaceAll("\\D", "") : ""));
					System.out.println("--Area [Sq. Ft.]: " + (size >= 3 ? tab[2].replaceAll("\\D", "") : ""));
				}
				Element price = doc.select("div.content #summary_price").first();
				if (price != null) {
					log.info("Apartment details found (price).");
					String pr = price.text();
					// we are sure that only one price is available
					System.out.println("--Price: " + pr.replaceAll("[^\\$|\\.|\\d]", "").trim());
					System.out.println("--Term: " + pr.replaceAll("\\$|,|\\d", "").trim());
				}
				Element deposit = doc.select("div.content #summary_other_pricing").first();
				if (deposit != null) {
					log.info("Apartment details found (deposit).");
					String[] tab = deposit.text().split("\\|");
					int size = tab.length;
					System.out.println("--Deposit: " + (size >= 1 ? tab[0].split(" ")[0].replaceAll("[^\\$|\\.|\\d]", "") : ""));
				}
			}
			System.out.println("--Features: ");
			Elements features = doc.select("div#property_details ul>li");
			if (features.size() > 0) {
				log.info("Apartment features found.");
				for (Element feature : features) {
					System.out.println("----" + feature.text());
				}
			}
			Element description = doc.select("div#property_details h3:containsOwn(description)~p").first();
			if (description != null) {
				log.info("Apartment description found.");
				System.out.println("--Description: " + description.text());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
