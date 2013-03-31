package pl.edu.pk.olap.realestate.core.parser;

import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.pk.olap.realestate.config.ConfigurationConstants;
import pl.edu.pk.olap.realestate.util.StatesAbbreviationsMap;
import pl.edu.pk.olap.realestate.util.TextUtils;

// regu³y dla price i deposit zrobiæ w metodzie (klasy abstract parser albo xxxUtils, bo liczbowy format trzeba ujednoliciæ NumberFormat)
// problem z dolarem przed cen¹ i np 400 i 400.00 (wszystkie liczby maj¹ problem z kropk¹) oraz np $400-$600, czyli pauza
// poprawiæ wyci¹ganie street z rentals
// dane na temat community i schools  braæ z pierwszego elementu (w parse page jeœli parsujemy pierwszy item to wo³aæ parseSchools i parse Demographics)
// rozró¿niæ kupno i sprzeda¿
// dodaæ timestamp (to chyba janek zrobi)
// ka¿dy parser pisze do swojego pliku.
// parsowanie ceny wyodrêbniæ do nowej metody (a nawet u¿ycie metody formatNumber)
/**
 * 
 * @author b4rt3k
 * 
 */
public class RentalsParser extends AbstractParser {

	private static Logger log = Logger.getLogger(RentalsParser.class);
	private static Map<String, String> statesMap = new StatesAbbreviationsMap();

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
		if (e != null) {
			String text = e.text().trim();
			try {
				return Integer.parseInt(text.substring(text.lastIndexOf(" ") + 1, text.length()));
			} catch (NumberFormatException e1) {
				return 1;
			}
		}
		return 1;
	}

	@Override
	public String pageNumberHttpQuery(int page) {
		return "?page=" + page;
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
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}

	@Override
	public Element itemLocalitySelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractLocality(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text().split(",")[0]);
		}
		return null;
	}

	@Override
	public Element itemRegionSelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractRegion(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 1) {
				String[] innerTab = tab[1].trim().split(" ");
				if (innerTab.length > 0) {
					String state = TextUtils.emptyStringToNull(innerTab[0]);
					return state != null ? statesMap.get(state) : state;
				}
			}
		}
		return null;
	}

	@Override
	public Element itemPostalCodeSelector(Element root) {
		return root.select("div.listing_details .listing_location").first();
	}

	@Override
	public String extractPostalCode(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 1) {
				String[] innerTab = tab[1].trim().split(" ");
				if (innerTab.length > 1) {
					String postal = TextUtils.emptyStringToNull(innerTab[1]);
					return postal;
				}
			}
		}
		return null;
	}

	@Override
	public Element itemPhoneNumberSelector(Element root) {
		return root.select("div.listing_details .listing_phone").first();
	}

	@Override
	public String extractPhoneNumber(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
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
		if (e != null) {
			String tab[] = e.text().split("\\|");
			if (tab.length > 0) {
				return TextUtils.emptyStringToNull(tab[0]);
			}
		}
		return null;
	}

	@Override
	public Element itemStyleSelector(Element root) {
		return root.select("table.floorPlanTable tr:last-child>td:eq(0)").first();
	}

	@Override
	public String extractStyle(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
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
			if ("td".equalsIgnoreCase(e.tagName())) {
				try {
					return TextUtils.formatNumber(e.text(), 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse beds count to number.", e1);
					return null;
				}
			} else {
				String[] tab = e.text().split("\\|");
				if (tab.length > 0) {
					try {
						return TextUtils.formatNumber(tab[0], 0);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse beds count to number.", e1);
						return null;
					}
				}
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
			if ("td".equalsIgnoreCase(e.tagName())) {
				try {
					return TextUtils.formatNumber(e.text(), 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse baths count to number.", e1);
					return null;
				}
			} else {
				String[] tab = e.text().split("\\|");
				if (tab.length > 1) {
					try {
						return TextUtils.formatNumber(tab[1], 0);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse baths count to number.", e1);
						return null;
					}
				}
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
			if ("td".equalsIgnoreCase(e.tagName())) {
				try {
					return TextUtils.formatNumber(e.text(), 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse area to number.", e1);
					return null;
				}
			} else {
				String[] tab = e.text().split("\\|");
				if (tab.length > 2) {
					try {
						return TextUtils.formatNumber(tab[2], 0);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse area to number.", e1);
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	public Element itemPriceSelector(Element root) {
		Element price = root.select("table.floorPlanTable tr:last-child>td:eq(5)").first();
		if (price == null) {
			return root.select("div.content #summary_price>strong").first();
		}
		return price;
	}

	@Override
	public String extractPrice(Element e) {
		if (e != null) {
			if ("td".equalsIgnoreCase(e.tagName())) {
				String[] tab = e.text().split("-");
				if (tab.length > 1) {
					try {
						return TextUtils.formatNumber(tab[1], 2);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse price to number.", e1);
						return null;
					}
				}
				if (tab.length > 0) {
					try {
						return TextUtils.formatNumber(tab[0], 2);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse price to number.", e1);
						return null;
					}
				}
			} else {
				String[] tab = e.text().split("-");
				if (tab.length > 1) {
					try {
						return TextUtils.formatNumber(tab[1], 2);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse price to number.", e1);
						return null;
					}
				}
				if (tab.length > 0) {
					try {
						return TextUtils.formatNumber(tab[0], 2);
					} catch (ParseException e1) {
						this.getLogger().error("Cannot parse price to number.", e1);
						return null;
					}
				}
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
			if ("td".equalsIgnoreCase(e.tagName())) {
				return TextUtils.emptyStringToNull(e.text().toLowerCase());
			} else {
				return TextUtils.emptyStringToNull(TextUtils.deleteNumber(e.text().toLowerCase()));
			}
		}
		return null;
	}

	@Override
	public Element itemDepositSelector(Element root) {
		Element deposit = root.select("table.floorPlanTable tr:last-child>td:eq(7)").first();
		if (deposit == null) {
			return root.select("div.content #summary_other_pricing>strong").first();
		}
		return deposit;
	}

	@Override
	public String extractDeposit(Element e) {
		if (e != null) {
			if ("td".equalsIgnoreCase(e.tagName())) {
				try {
					return TextUtils.formatNumber(e.text(), 2);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse deposit to number.", e1);
					return null;
				}
			} else {
				try {
					return TextUtils.formatNumber(e.text(), 2);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse deposit to number.", e1);
					return null;
				}
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
			return TextUtils.emptyStringToNull(e.text());
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
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}
}
