package pl.edu.pk.olap.realestate.util;

import java.text.NumberFormat;
import java.text.ParseException;

public class TextUtils {
	public static String emptyStringToNull(String input) {
		if (input.trim().isEmpty()) {
			return null;
		}
		return input.trim();
	}

	public static String formatNumber(String in, int fractionDigits) throws ParseException {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(fractionDigits);
		nf.setMaximumFractionDigits(fractionDigits);
		nf.setGroupingUsed(false);
		if (in.contains(".")) {
			in = in.substring(0, in.lastIndexOf("."));
		}
		return emptyStringToNull(nf.format(nf.parse(in.trim().replaceAll("[^\\d]", ""))));
	}

	public static String deleteNumber(String in) {
		return in.trim().replaceAll("\\$|,|\\.|\\d", "");
	}

	public static void main(String[] args) throws ParseException {
		String nr = "$1,200.00";
		System.out.println(formatNumber(nr, 0));
	}
}
