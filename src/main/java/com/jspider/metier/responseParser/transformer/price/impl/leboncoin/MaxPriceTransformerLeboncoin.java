package com.jspider.metier.responseParser.transformer.price.impl.leboncoin;

import org.jsoup.nodes.Element;

import com.jspider.metier.responseParser.transformer.Transformer;

public class MaxPriceTransformerLeboncoin implements Transformer {

	@Override
	public String transform(String arg) {
		Integer price = Integer.parseInt(arg);
		String ret = null;
		if (price <= 0) {
			ret = "0";
		} else if (price <= 250) {
			ret = "1";
		} else if (price <= 500) {
			ret = "2";
		} else if (price <= 750) {
			ret = "3";
		} else if (price <= 1000) {
			ret = "4";
		} else if (price <= 1500) {
			ret = "5";
		} else if (price <= 2000) {
			ret = "6";
		} else if (price <= 2500) {
			ret = "7";
		} else if (price <= 3000) {
			ret = "8";
		} else if (price <= 3500) {
			ret = "9";
		} else if (price <= 4000) {
			ret = "10";
		} else if (price <= 4500) {
			ret = "11";
		} else if (price <= 5000) {
			ret = "12";
		} else if (price <= 5500) {
			ret = "13";
		} else if (price <= 6000) {
			ret = "14";
		} else if (price <= 6500) {
			ret = "15";
		} else if (price <= 7000) {
			ret = "16";
		} else if (price <= 7500) {
			ret = "17";
		} else if (price <= 8000) {
			ret = "18";
		} else if (price <= 8500) {
			ret = "19";
		} else if (price <= 9000) {
			ret = "20";
		} else if (price <= 9500) {
			ret = "21";
		} else if (price <= 10000) {
			ret = "22";
		} else if (price <= 11000) {
			ret = "23";
		} else if (price <= 12000) {
			ret = "24";
		} else if (price <= 13000) {
			ret = "25";
		} else if (price <= 14000) {
			ret = "26";
		} else if (price <= 15000) {
			ret = "27";
		} else if (price <= 17500) {
			ret = "28";
		} else if (price <= 20000) {
			ret = "29";
		} else if (price <= 22000) {
			ret = "30";
		} else if (price <= 25000) {
			ret = "31";
		} else if (price <= 27500) {
			ret = "32";
		} else if (price <= 30000) {
			ret = "33";
		} else if (price <= 32500) {
			ret = "34";
		} else if (price <= 35000) {
			ret = "35";
		} else if (price <= 37500) {
			ret = "36";
		} else if (price <= 40000) {
			ret = "37";
		} else if (price <= 42500) {
			ret = "38";
		} else if (price <= 45000) {
			ret = "39";
		} else if (price <= 47500) {
			ret = "40";
		} else if (price <= 50000) {
			ret = "41";
		}
		return ret;
	}

	@Override
	public String transform(Element e) {
		return null;
	}

}
