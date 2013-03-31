package pl.edu.pk.olap.realestate.core.parser;

import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.pk.olap.realestate.config.ConfigurationConstants;
import pl.edu.pk.olap.realestate.util.StatesAbbreviationsMap;
import pl.edu.pk.olap.realestate.util.TextUtils;

public class TruliaParser extends AbstractParser {
	private static Logger log = Logger.getLogger(TruliaParser.class);
	private static Map<String, String> statesMap = new StatesAbbreviationsMap();

	@Override
	public Logger getLogger() {
		return TruliaParser.log;
	}

	@Override
	public String getPortalName() {
		return "Trulia";
	}

	@Override
	public String getUrl() {
		return ConfigurationConstants.TRULIA_URL;
	}

	@Override
	public Elements statesLinksSelector(Element root) {
		return root.select("div.content table>tbody>tr>td:eq(0)>div:eq(2)>span>a[href]");
	}

	@Override
	public Elements citiesLinksSelector(Element root) {
		return root.select("div.content table>tbody>tr>td:eq(0)>div:eq(2)>span>a[href]");
	}

	@Override
	public Element pagesCountSelector(Element root) {
		return root.select("div.bottom table.paging>tbody>tr>td div.paging_string a:last-child").first();
	}

	@Override
	public int extractPagesCount(Element e) {
		if (e != null) {
			String text = e.text().trim();
			try {
				return Integer.parseInt(text);
			} catch (NumberFormatException e1) {
				return 1;
			}
		}
		return 1;
	}

	@Override
	public String pageNumberHttpQuery(int page) {
		return "" + page + "_p";
	}

	@Override
	public Elements singleItemBaseInfoSelector(Element root) {
		return root.select("#right_content #center_rail #results_tab_for_rent_rows>div.srp_row");
	}

	@Override
	public Element itemNameSelector(Element root) {
		return root.select(".address_section>a.address[href]").first();
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
		return root.select(".property_location").first();
	}

	@Override
	public String extractLocality(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 1) {
				String[] innerTab = tab[1].trim().split(" ");
				if (innerTab.length > 0) {
					return TextUtils.emptyStringToNull(innerTab[0]);
				}
			}
		}
		return null;
	}

	@Override
	public Element itemRegionSelector(Element root) {
		return root.select(".property_location").first();
	}

	@Override
	public String extractRegion(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 1) {
				String[] innerTab = tab[1].trim().split(" ");
				if (innerTab.length > 1) {
					String state = TextUtils.emptyStringToNull(innerTab[1]);
					return state != null ? statesMap.get(state) : state;
				}
			}
		}
		return null;
	}

	@Override
	public Element itemPostalCodeSelector(Element root) {
		return root.select(".property_location").first();
	}

	@Override
	public String extractPostalCode(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 1) {
				String[] innerTab = tab[1].trim().split(" ");
				if (innerTab.length > 2) {
					return TextUtils.emptyStringToNull(innerTab[2]);
				}
			}
		}
		return null;
	}

	@Override
	public Element itemPhoneNumberSelector(Element root) {
		return root.select(".community_contact_phone").first();
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
		return root.select(".address_section>a.address[href]").first();
	}

	@Override
	public Element itemStreetSelector(Element root) {
		return root.select(".property_location").first();
	}

	@Override
	public String extractStreet(Element e) {
		if (e != null) {
			String tab[] = e.text().split(",");
			if (tab.length > 0) {
				return TextUtils.emptyStringToNull(tab[0]);
			}
		}
		return null;
	}

	@Override
	public Element itemStyleSelector(Element root) {
		return root.select("table.floorplan_table tr:last-child>td:eq(0)").first();
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
		Element beds = root.select("table.floorplan_table tr:last-child>td:eq(1)").first();
		if (beds == null) {
			return root.select(".listing_container ul.listing_info>li:containsOwn(Bedroom)").first();
		}
		return beds;
	}

	@Override
	public String extractBedsCount(Element e) {
		if (e != null) {
			try {
				return TextUtils.formatNumber(e.text(), 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse beds count to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Element itemBathroomsCountSelector(Element root) {
		Element beds = root.select("table.floorplan_table tr:last-child>td:eq(2)").first();
		if (beds == null) {
			return root.select(".listing_container ul.listing_info>li:containsOwn(Bathroom)").first();
		}
		return beds;
	}

	@Override
	public String extractBathroomsCount(Element e) {
		if (e != null) {
			try {
				return TextUtils.formatNumber(e.text(), 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse bathrooms count to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Element itemAreaSelector(Element root) {
		Element beds = root.select("table.floorplan_table tr:last-child>td:eq(3)").first();
		if (beds == null) {
			return root.select(".listing_container ul.listing_info>li:containsOwn(sqft)").first();
		}
		return beds;
	}

	@Override
	public String extractArea(Element e) {
		if (e != null) {
			try {
				return TextUtils.formatNumber(e.text(), 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse area to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Element itemPriceSelector(Element root) {
		Element beds = root.select("table.floorplan_table tr:last-child>td:eq(4)").first();
		if (beds == null) {
			return root.select(".listing_container ul.listing_info>li:containsOwn(Price)").first();
		}
		return beds;
	}

	@Override
	public String extractPrice(Element e) {
		if (e != null) {
			try {
				return TextUtils.formatNumber(e.text(), 2);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Element itemTermSelector(Element root) {
		return root.select(".property_price").first();
	}

	@Override
	public String extractTerm(Element e) {
		if (e != null) {
			String text = TextUtils.emptyStringToNull(e.text());
			if (text != null && (text.toLowerCase().contains("mo"))) {
				return "per month";
			}
		}
		return null;
	}

	@Override
	public Element itemDepositSelector(Element root) {
		return root.select(".listing_container ul.listing_info>li:containsOwn(Deposit)").first();
	}

	@Override
	public String extractDeposit(Element e) {
		if (e != null) {
			try {
				return TextUtils.formatNumber(e.text(), 2);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse deposit to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Elements itemFeaturesSelector(Element root) {
		Elements list = root.select("ul.features_list>li");
		if (list == null || list.size() == 0) {
			return root.select("ul.listing_info>li");
		}
		return list;
	}

	@Override
	public String extractFeature(Element e) {
		if (e != null) {
			/** replace is needed because of weird characters inside list items */
			return TextUtils.emptyStringToNull(e.text().replaceAll("•\u00a0", ""));
		}
		return null;
	}

	@Override
	public Element itemDescriptionSelector(Element root) {
		Element desc = root.select("#property_description_showing").first();
		if (desc == null) {
			return root.select(".listing_description_module").first();
		}
		return desc;
	}

	@Override
	public String extractDescription(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
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
					parseItem(itemDetailsLinkSelector(item).attr("abs:href"), index++);
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
			System.out.println("--Reigon: " + extractRegion(itemRegionSelector(doc)));
			System.out.println("--Locality: " + extractLocality(itemLocalitySelector(doc)));
			System.out.println("--Postal Code: " + extractPostalCode(itemPostalCodeSelector(doc)));
			System.out.println("--Phone Number: " + extractPhoneNumber(itemPhoneNumberSelector(doc)));
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
