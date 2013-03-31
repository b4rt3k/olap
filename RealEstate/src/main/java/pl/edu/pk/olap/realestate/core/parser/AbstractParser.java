package pl.edu.pk.olap.realestate.core.parser;

import org.apache.log4j.Logger;
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
public abstract class AbstractParser implements HttpParser {
	@Autowired
	protected ConfigReader reader;
	protected int timeout;

	public abstract Logger getLogger();

	/**
	 * Method returns portal name
	 * 
	 * @return
	 */
	public abstract String getPortalName();

	/**
	 * Method returns portal URL
	 * 
	 * @return
	 */
	public abstract String getUrl();

	/**
	 * Method return elements containing states links
	 * 
	 * @return
	 */
	public abstract Elements statesLinksSelector(Element root);

	/**
	 * Method return elements containing cities links
	 * 
	 * @return
	 */
	public abstract Elements citiesLinksSelector(Element root);

	/**
	 * Method return element containing pages count
	 * 
	 * @return
	 */
	public abstract Element pagesCountSelector(Element root);

	/**
	 * Method extract pages count from element whose selector was returned by
	 * {@link #pagesCountSelector}
	 * 
	 * @param e
	 * @return
	 */
	public abstract int extractPagesCount(Element e);

	/**
	 * Method return part of HTTP query containing page number
	 * 
	 * @return
	 */
	public abstract String pageNumberHttpQuery(int page);

	/**
	 * Method return items on paginated list
	 * 
	 * @return
	 */
	public abstract Elements singleItemBaseInfoSelector(Element root);

	/**
	 * Method return element containing item name
	 * 
	 * @return
	 */
	public abstract Element itemNameSelector(Element root);

	/**
	 * Method extract item name from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractName(Element e);

	/**
	 * Method return element containing item locality
	 * 
	 * @return
	 */
	public abstract Element itemLocalitySelector(Element root);

	/**
	 * Method extract locality from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractLocality(Element e);

	/**
	 * Method return element containing item region
	 * 
	 * @return
	 */
	public abstract Element itemRegionSelector(Element root);

	/**
	 * Method extract region from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractRegion(Element e);

	/**
	 * Method return element containing item postal code
	 * 
	 * @return
	 */
	public abstract Element itemPostalCodeSelector(Element root);

	/**
	 * Method extract postal code from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractPostalCode(Element e);

	/**
	 * Method return element containing item phone number
	 * 
	 * @return
	 */
	public abstract Element itemPhoneNumberSelector(Element root);

	/**
	 * Method extract postal code from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractPhoneNumber(Element e);

	/**
	 * Method return single item details link
	 * 
	 * 
	 * @return
	 */
	public abstract Element itemDetailsLinkSelector(Element root);

	/**
	 * Method return element containing item street
	 * 
	 * @return
	 */
	public abstract Element itemStreetSelector(Element root);

	/**
	 * Method extract street from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractStreet(Element e);

	/**
	 * Method return element containing item style
	 * 
	 * @return
	 */
	public abstract Element itemStyleSelector(Element root);

	/**
	 * Method extract style from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractStyle(Element e);

	/**
	 * Method return element containing item beds count
	 * 
	 * @return
	 */
	public abstract Element itemBedsCountSelector(Element root);

	/**
	 * Method extract beds count from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractBedsCount(Element e);

	/**
	 * Method return element containing item bathrooms count
	 * 
	 * @return
	 */
	public abstract Element itemBathroomsCountSelector(Element root);

	/**
	 * Method extract bathrooms count from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractBathroomsCount(Element e);

	/**
	 * Method return element containing item area
	 * 
	 * @return
	 */
	public abstract Element itemAreaSelector(Element root);

	/**
	 * Method extract area from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractArea(Element e);

	/**
	 * Method return element containing item price
	 * 
	 * @return
	 */
	public abstract Element itemPriceSelector(Element root);

	/**
	 * Method extract price from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractPrice(Element e);

	/**
	 * Method return element containing item term
	 * 
	 * @return
	 */
	public abstract Element itemTermSelector(Element root);

	/**
	 * Method extract term from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractTerm(Element e);

	/**
	 * Method return element containing item deposit
	 * 
	 * @return
	 */
	public abstract Element itemDepositSelector(Element root);

	/**
	 * Method extract deposit from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractDeposit(Element e);

	/**
	 * Method return elements containing item features
	 * 
	 * @return
	 */
	public abstract Elements itemFeaturesSelector(Element root);

	/**
	 * Method extract feature from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractFeature(Element e);

	/**
	 * Method return element containing item description
	 * 
	 * @return
	 */
	public abstract Element itemDescriptionSelector(Element root);

	/**
	 * Method extract description from given element
	 * 
	 * @param e
	 * @return
	 */
	public abstract String extractDescription(Element e);

	@Override
	public void parse() {
		getLogger().info(getPortalName() + " parsing started");
		timeout = Integer.parseInt(reader.getProperty(ConfigurationConstants.HTTP_CONNECTION_TIMEOUT_MILLIS));
		Document doc = null;
		try {
			doc = Jsoup.connect(reader.getProperty(getUrl())).timeout(timeout).userAgent("Mozilla").get();
			Elements links = statesLinksSelector(doc);
			if (links != null && links.size() > 0) {
				getLogger().info("States links found.");
				for (Element link : links) {
					parseState(link.attr("abs:href"), link.text());
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	protected void parseState(String url, String stateName) {
		getLogger().info("Parsing state: " + stateName);
		System.out.println("------------------------------------------");
		System.out.println(stateName);
		System.out.println("------------------------------------------");
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements links = citiesLinksSelector(doc);
			if (links != null && links.size() > 0) {
				getLogger().info("Cities links found.");
				for (Element link : links) {
					parseCity(link.attr("abs:href"), link.text());
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	protected void parseCity(String url, String cityName) {
		getLogger().info("Parsing city: " + cityName);
		System.out.println(cityName);
		int pages = 1;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Element elem = pagesCountSelector(doc);
			if (elem != null) {
				pages = extractPagesCount(elem);
				getLogger().info("Pages count found: " + pages);
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}

		for (int i = 0; i < pages; i++) {
			if (!url.endsWith("/")) {
				url += "/";
			}
			parsePage(url + pageNumberHttpQuery(i + 1), cityName, (i + 1));
		}

	}

	protected void parsePage(String url, String city, int page) {
		getLogger().info("Parsing page: " + page + ", city: " + city);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			Elements items = singleItemBaseInfoSelector(doc);
			int index = 0;
			if (items != null && items.size() > 0) {
				getLogger().info("Apartments base info found.");
				for (Element item : items) {
					System.out.println("--Portal: " + getPortalName());
					System.out.println("--Name: " + extractName(itemNameSelector(item)));
					// System.out.println("--Reigon: " +
					// extractRegion(itemRegionSelector(item)));
					// System.out.println("--Locality: " +
					// extractLocality(itemLocalitySelector(item)));
					// System.out.println("--Postal Code: " +
					// extractPostalCode(itemPostalCodeSelector(item)));
					// System.out.println("--Phone Number: " +
					// extractPhoneNumber(itemPhoneNumberSelector(item)));
					// parseItem(itemDetailsLinkSelector(item).attr("abs:href"),
					// index++);
					System.out.println();
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	protected void parseItem(String url, int index) {
		getLogger().info("Parsing item: " + index);
		Document doc = null;
		try {
			// dodac loggery
			doc = Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").get();
			System.out.println("--Street: " + extractStreet(itemStreetSelector(doc)));
			System.out.println("--Style: " + extractStyle(itemStyleSelector(doc)));
			System.out.println("--Beds: " + extractBedsCount(itemBedsCountSelector(doc)));
			System.out.println("--Bathrooms: " + extractBathroomsCount(itemBathroomsCountSelector(doc)));
			System.out.println("--Area: " + extractArea(itemAreaSelector(doc)));
			System.out.println("--Price: " + extractPrice(itemPriceSelector(doc)));
			System.out.println("--Term: " + extractTerm(itemTermSelector(doc)));
			System.out.println("--Deposit: " + extractDeposit(itemDepositSelector(doc)));
			System.out.println("--Features: ");
			Elements features = itemFeaturesSelector(doc);
			if (features != null && features.size() > 0) {
				getLogger().info("Apartment features found.");
				for (Element feature : features) {
					System.out.println("----" + extractFeature(feature));
				}
			}
			Element description = itemDescriptionSelector(doc);
			if (description != null) {
				getLogger().info("Apartment description found.");
				System.out.println("--Description: " + extractDescription(description));
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
