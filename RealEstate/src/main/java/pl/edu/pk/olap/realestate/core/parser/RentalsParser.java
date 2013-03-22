package pl.edu.pk.olap.realestate.core.parser;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.pk.olap.realestate.config.ConfigurationConstants;
import pl.edu.pk.olap.realestate.util.StatesAbbreviationsMap;

// jeœli wszystkie parsery bêd¹ mia³y podobn¹ strukturê, to zrobiæ klasê abstrakcyjn¹, a w podklasach podmieniaæ tylko selektory!!!
// w parsePage i parseItem zprawdzac wszystkie selektory czy znajduj¹ odpowiednie elementy na stronie
// ujednoliciæ wielkoœæ znakó (tolowercase)
// regu³y dla price i deposit zrobiæ w metodzie (klasy abstract parser albo xxxUtils, bo liczbowy format trzeba ujednoliciæ NumberFormat)
// wszystko trimowaæ
// problem z dolarem przed cen¹ i np 400 i 400.00 (wszystkie liczby maj¹ problem z kropk¹) oraz np $400-$600, czyli pauza
// cenê liczyæ œredni¹
// poprawiæ wyci¹ganie street z rentals
// zrobiæ metodê która zamienia puste stringi na nulle
/**
 * 
 * @author b4rt3k
 * 
 */
public class RentalsParser extends AbstractParser {

	private static Logger log = Logger.getLogger(RentalsParser.class);
	private static Map<String, String> statesMap = new StatesAbbreviationsMap();
	private static final String TD = "td";

	@Override
	public Logger getLogger() {
		return RentalsParser.log;
	}

	@Override
	public String getPortalName() {
		return "Rentals";
	}

	@Override
	public String getUrl() {
		return ConfigurationConstants.RENTALS_URL;
	}

	@Override
	public Elements statesLinksSelector(Element root) {
		return root.select("div#search_locations_popup ul.search_locations_states>li>a[href]");
	}

	@Override
	public Elements citiesLinksSelector(Element root) {
		return root.select("div.content div#state_links>ul>li>a[href]");
	}

	@Override
	public Element pagesCountSelector(Element root) {
		return root.select("div#search_results_container div.pagination>h2").first();
	}

	@Override
	public int extractPagesCount(Element e) {
		String text = e.text().trim();
		return Integer.parseInt(text.substring(text.lastIndexOf(" ") + 1, text.length()));
	}

	@Override
	public String pageNumberHttpQuery() {
		return "?page=";
	}

	@Override
	public Elements singleItemBaseInfoSelector(Element root) {
		return root.select("div#search_results_container div#search_results div.result");
	}

	@Override
	public Element itemNameSelector(Element root) {
		return root.select("div.listing_details .listing_name>a[href]").first();
	}

	@Override
	public String extractName(Element e) {
		return e.text().trim();
	}

	@Override
	public Element itemLocalitySelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractLocality(Element e) {
		return e.text().split(",")[0].trim();
	}

	@Override
	public Element itemRegionSelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractRegion(Element e) {
		return statesMap.get(e.text().split(",")[1].trim().split(" ")[0].trim());
	}

	@Override
	public Element itemPostalCodeSelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractPostalCode(Element e) {
		return e.text().split(",")[1].trim().split(" ")[1].trim();
	}

	@Override
	public Element itemPhoneNumberSelector(Element root) {
		return root.select("div.listing_details .listing_phone").first();
	}

	@Override
	public String extractPhoneNumber(Element e) {
		if (e != null) {
			return e.text().trim();
		}
		return null;
	}

	@Override
	public Element itemDetailsLinkSelector(Element root) {
		return root.select("div.listing_details .listing_name>a[href]").first();
	}

	@Override
	public Element itemStreetSelector(Element root) {
		return root.select("div.content #summary_address").first();
	}

	@Override
	public String extractStreet(Element e) {
		return e.text().split("\\|")[0].trim();
	}

	@Override
	public Element itemStyleSelector(Element root) {
		return root.select("table.floorPlanTable tr:last-child>td:eq(0)").first();
	}

	@Override
	public String extractStyle(Element e) {
		if (e != null) {
			return e.text().trim();
		}
		return null;
	}

	@Override
	public Element itemBedsCountSelector(Element root) {
		Element beds = root.select("table.floorPlanTable tr:last-child>td:eq(1)").first();
		if (beds == null) {
			return root.select("div.content #summary_floorplan").first();
		}
		return beds;
	}

	@Override
	public String extractBedsCount(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim();
			} else {
				String[] tab = e.text().split("\\|");
				return tab.length >= 1 ? tab[0].replaceAll("\\D", "") : null;
			}
		}
		return null;
	}

	@Override
	public Element itemBathroomsCountSelector(Element root) {
		Element bathrooms = root.select("table.floorPlanTable tr:last-child>td:eq(2)").first();
		if (bathrooms == null) {
			return root.select("div.content #summary_floorplan").first();
		}
		return bathrooms;
	}

	@Override
	public String extractBathroomsCount(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim();
			} else {
				String[] tab = e.text().split("\\|");
				return tab.length >= 2 ? tab[1].replaceAll("\\D", "") : null;
			}
		}
		return null;
	}

	@Override
	public Element itemAreaSelector(Element root) {
		Element area = root.select("table.floorPlanTable tr:last-child>td:eq(4)").first();
		if (area == null) {
			return root.select("div.content #summary_floorplan").first();
		}
		return area;
	}

	@Override
	public String extractArea(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim();
			} else {
				String[] tab = e.text().split("\\|");
				return tab.length >= 3 ? tab[2].replaceAll("\\D", "") : null;
			}
		}
		return null;
	}

	@Override
	public Element itemPriceSelector(Element root) {
		Element price = root.select("table.floorPlanTable tr:last-child>td:eq(5)").first();
		if (price == null) {
			return root.select("div.content #summary_price").first();
		}
		return price;
	}

	@Override
	public String extractPrice(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim().replaceAll("[^\\$|\\.|\\d|-]", "");
			} else {
				return e.text().trim().replaceAll("[^\\$|\\.|\\d]", "");
			}
		}
		return null;
	}

	@Override
	public Element itemTermSelector(Element root) {
		Element term = root.select("table.floorPlanTable tr:last-child>td:eq(6)").first();
		if (term == null) {
			return root.select("div.content #summary_price").first();
		}
		return term;
	}

	@Override
	public String extractTerm(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim();
			} else {
				return e.text().trim().replaceAll("\\$|,|\\d", "");
			}
		}
		return null;
	}

	@Override
	public Element itemDepositSelector(Element root) {
		Element deposit = root.select("table.floorPlanTable tr:last-child>td:eq(6)").first();
		if (deposit == null) {
			return root.select("div.content #summary_other_pricing").first();
		}
		return deposit;
	}

	@Override
	public String extractDeposit(Element e) {
		if (e != null) {
			if (TD.equalsIgnoreCase(e.tagName())) {
				return e.text().trim().replaceAll("[^\\$|\\.|\\d]", "");
			} else {
				String[] tab = e.text().trim().split("\\|");
				return tab.length >= 1 ? tab[0].split(" ")[0].replaceAll("[^\\$|\\.|\\d]", "") : null;
			}
		}
		return null;
	}

	@Override
	public Elements itemFeaturesSelector(Element root) {
		return root.select("div#property_details ul>li");
	}

	@Override
	public String extractFeature(Element e) {
		if (e != null) {
			return e.text().trim();
		}
		return null;
	}

	@Override
	public Element itemDescriptionSelector(Element root) {
		return root.select("div#property_details h3:containsOwn(description)~p").first();
	}

	@Override
	public String extractDescription(Element e) {
		if (e != null) {
			return e.text().trim();
		}
		return null;
	}
}
