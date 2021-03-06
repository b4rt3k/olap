package pl.edu.pk.olap.realestate.core.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.pk.olap.realestate.config.ConfigurationConstants;
import pl.edu.pk.olap.realestate.util.TextUtils;

/**
 * @author b4rt3k
 * 
 */
public class ApartmentGuideParser extends AbstractParser {
	private static Logger log = Logger.getLogger(ApartmentGuideParser.class);

	@Override
	public Logger getLogger() {
		return ApartmentGuideParser.log;
	}

	@Override
	public String getPortalName() {
		return "ApartmentGuide";
	}

	@Override
	public String getUrl() {
		return ConfigurationConstants.APARTMENTGUIDE_URL;
	}

	@Override
	public Elements statesLinksSelector(Element root) {
		return root.select("ul.browse_links>li>a[href]");
	}

	@Override
	public Elements citiesLinksSelector(Element root) {
		return root.select(".padding_box ul>li>a[href]");
	}

	@Override
	public Element pagesCountSelector(Element root) {
		return root.select("div.pagination>ol>li:last-child a").first();
	}

	@Override
	public int extractPagesCount(Element e) {
		if (e != null) {
			try {
				return Integer.parseInt(new URL(e.attr("abs:href")).getQuery().split("=")[1]);
			} catch (NumberFormatException | MalformedURLException e1) {
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
		return root.select("div#results div#resultWrapper div.result");
	}

	@Override
	public Element itemNameSelector(Element root) {
		return root.select("div.column2>h3>a[href]").first();
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
		return root.select("div.column2>ul>li.display_address>span[itemprop=addressLocality]").first();
	}

	@Override
	public String extractLocality(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}

	@Override
	public Element itemRegionSelector(Element root) {
		return root.select("div.column2>ul>li.display_address>span[itemprop=addressRegion]").first();
	}

	@Override
	public String extractRegion(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}

	@Override
	public Element itemPostalCodeSelector(Element root) {
		return root.select("div.column2>ul>li.display_address>span[itemprop=postalCode]").first();
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
		return root.select("ul.listing_controls>li.phone_number.large.non_sem_number").first();
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
		return root.select("div.column2>h3>a[href]").first();
	}

	@Override
	public Element itemStreetSelector(Element root) {
		return root.select("div#details_header span[itemprop=streetAddress]").first();
	}

	@Override
	public String extractStreet(Element e) {
		if (e != null) {
			String street = TextUtils.emptyStringToNull(e.text().replaceAll(",", ""));
			return street.endsWith(",") ? street.substring(0, street.length() - 1) : street;
		}
		return null;
	}

	@Override
	public Element itemStyleSelector(Element root) {
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(0)").first();
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
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(1)").first();
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
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(2)").first();
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
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(4)").first();
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
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(5)").first();
	}

	@Override
	public String extractPrice(Element e) {
		if (e != null) {
			String[] tab = e.text().split("-");
			if (tab.length > 1) {
				try {
					return TextUtils.formatNumber(tab[1], 2);
				} catch (ParseException e1) {
					this.getLogger().error("Cannot parse price to number.", e1);
					return null;
				}
			}
			try {
				return TextUtils.formatNumber(tab[0], 2);
			} catch (ParseException e1) {
				this.getLogger().error("Cannot parse price to number.", e1);
				return null;
			}
		}
		return null;
	}

	@Override
	public Element itemTermSelector(Element root) {
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(6)").first();
	}

	@Override
	public String extractTerm(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text().toLowerCase());
		}
		return null;
	}

	@Override
	public Element itemDepositSelector(Element root) {
		return root.select("div#floorplans_and_pricing_tab table.unit_detail tr:last-child td:eq(7)").first();
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
		return root.select("div#apartment_features_tab ul.features>li");
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
		return root.select("div#apartment_info_tab div#details_description #listing_description").first();
	}

	@Override
	public String extractDescription(Element e) {
		if (e != null) {
			return TextUtils.emptyStringToNull(e.text());
		}
		return null;
	}
}
