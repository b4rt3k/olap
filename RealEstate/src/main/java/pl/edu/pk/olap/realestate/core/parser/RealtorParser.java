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

public class RealtorParser extends AbstractParser {
	private static Logger log = Logger.getLogger(ApartmentGuideParser.class);
	private static Map<String, String> statesMap = new StatesAbbreviationsMap();

	@Override
	public Logger getLogger() {
		return RealtorParser.log;
	}

	@Override
	public String getPortalName() {
		return "Realtor";
	}

	@Override
	public String getUrl() {
		return ConfigurationConstants.REALTOR_URL;
	}

	@Override
	public Elements statesLinksSelector(Element root) {
		return root.select("#ListByState table>tbody>tr>td>a[href]");
	}

	@Override
	public Elements citiesLinksSelector(Element root) {
		return root.select("div.wrap div.row ul.list-columns>li>a[href]");
	}

	@Override
	public Element pagesCountSelector(Element root) {
		Elements list = root.select("#ListViewSortTop ol li");
		int size = 0;
		if ((size = list.size()) > 2) {
			return list.eq(size - 2).first();
		}
		return null;
	}

	@Override
	public int extractPagesCount(Element e) {
		if (e != null) {
			return Integer.parseInt(e.text().trim());
		}
		return 1;
	}

	@Override
	public String pageNumberHttpQuery() {
		return "pg-";
	}

	@Override
	public Elements singleItemBaseInfoSelector(Element root) {
		return root.select("#RentListView div.listing-group>div.listing");
	}

	@Override
	public Element itemNameSelector(Element root) {
		Element name = root.select("span.listing-community-name").first();
		if (name == null) {
			return root.select("span.listing-street-address").first();
		}
		return name;
	}

	@Override
	public String extractName(Element e) {
		if (e != null) {
			String name = TextUtils.emptyStringToNull(e.text());
			return name.endsWith(",") ? name.substring(0, name.length() - 1) : name;
		}
		return null;
	}

	@Override
	public Element itemLocalitySelector(Element root) {
		return root.select("span[itemprop=addressLocality]").first();
	}

	@Override
	public String extractLocality(Element e) {
		if (e != null) {
			String locality = TextUtils.emptyStringToNull(e.text().replaceAll(",", ""));
			return locality.endsWith(",") ? locality.substring(0, locality.length() - 1) : locality;
		}
		return null;
	}

	@Override
	public Element itemRegionSelector(Element root) {
		return root.select("span[itemprop=addressRegion]").first();
	}

	@Override
	public String extractRegion(Element e) {
		if (e != null) {
			String val = TextUtils.emptyStringToNull(e.text());
			return val != null ? statesMap.get(val) : val;
		}
		return null;
	}

	@Override
	public Element itemPostalCodeSelector(Element root) {
		return root.select("span[itemprop=postalCode]").first();
	}

	@Override
	public String extractPostalCode(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}

	@Override
	public Element itemPhoneNumberSelector(Element root) {
		return null;
	}

	@Override
	public String extractPhoneNumber(Element e) {
		return null;
	}

	@Override
	public Element itemDetailsLinkSelector(Element root) {
		return root.select("ul.listing-summary li.listing-location>a[href]").first();
	}

	@Override
	public Element itemStreetSelector(Element root) {
		return root.select("span.listing-street-address").first();
	}

	@Override
	public String extractStreet(Element e) {
		if (e != null) {
			String street = TextUtils.emptyStringToNull(e.text());
			return street.endsWith(",") ? street.substring(0, street.length() - 1) : street;
		}
		return null;
	}

	@Override
	public Element itemStyleSelector(Element root) {
		return null;
	}

	@Override
	public String extractStyle(Element e) {
		return null;
	}

	@Override
	public Element itemBedsCountSelector(Element root) {
		return root.select("ul.listing-summary li .listing-beds").first();
	}

	@Override
	public String extractBedsCount(Element e) {
		if (e != null) {
			String[] tab = e.text().split("-");
			if (tab.length > 1) {
				try {
					return TextUtils.formatNumber(tab[1], 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse price to number.", e1);
					return null;
				}
			}
			try {
				return TextUtils.formatNumber(tab[0], 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}

		}
		return null;
	}

	@Override
	public Element itemBathroomsCountSelector(Element root) {
		return root.select("ul.listing-summary li .listing-baths").first();
	}

	@Override
	public String extractBathroomsCount(Element e) {
		if (e != null) {
			String[] tab = e.text().split("-");
			if (tab.length > 1) {
				try {
					return TextUtils.formatNumber(tab[1], 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse price to number.", e1);
					return null;
				}
			}
			try {
				return TextUtils.formatNumber(tab[0], 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}

		}
		return null;
	}

	@Override
	public Element itemAreaSelector(Element root) {
		return root.select("ul.listing-summary li .listing-sqft").first();
	}

	@Override
	public String extractArea(Element e) {
		if (e != null) {
			String[] tab = e.text().split("-");
			if (tab.length > 1) {
				try {
					return TextUtils.formatNumber(tab[1], 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse price to number.", e1);
					return null;
				}
			}
			try {
				return TextUtils.formatNumber(tab[0], 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}

		}
		return null;
	}

	@Override
	public Element itemPriceSelector(Element root) {
		return root.select("ul.listing-summary li .listing-price").first();
	}

	@Override
	public String extractPrice(Element e) {
		if (e != null) {
			String[] tab = e.text().split("-");
			if (tab.length > 1) {
				try {
					return TextUtils.formatNumber(tab[1], 0);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse price to number.", e1);
					return null;
				}
			}
			try {
				return TextUtils.formatNumber(tab[0], 0);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}

		}
		return null;
	}

	@Override
	public Element itemTermSelector(Element root) {
		return null;
	}

	@Override
	public String extractTerm(Element e) {
		return null;
	}

	@Override
	public Element itemDepositSelector(Element root) {
		return null;
	}

	@Override
	public String extractDeposit(Element e) {
		return null;
	}

	@Override
	public Elements itemFeaturesSelector(Element root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractFeature(Element e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element itemDescriptionSelector(Element root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractDescription(Element e) {
		// TODO Auto-generated method stub
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
					System.out.println("--Reigon: " + extractRegion(itemRegionSelector(item)));
					System.out.println("--Locality: " + extractLocality(itemLocalitySelector(item)));
					System.out.println("--Postal Code: " + extractPostalCode(itemPostalCodeSelector(item)));
					System.out.println("--Phone Number: " + extractPhoneNumber(itemPhoneNumberSelector(item)));
					System.out.println("--Street: " + extractStreet(itemStreetSelector(item)));
					System.out.println("--Style: " + extractStyle(itemStyleSelector(item)));
					System.out.println("--Beds: " + extractBedsCount(itemBedsCountSelector(item)));
					System.out.println("--Bathrooms: " + extractBathroomsCount(itemBathroomsCountSelector(item)));
					System.out.println("--Area: " + extractArea(itemAreaSelector(item)));
					System.out.println("--Price: " + extractPrice(itemPriceSelector(item)));
					System.out.println("--Term: " + extractTerm(itemTermSelector(doc)));
					System.out.println("--Deposit: " + extractDeposit(itemDepositSelector(doc)));
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
			Elements features = itemFeaturesSelector(doc);
			System.out.println("--Features: ");
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
