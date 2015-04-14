package com.jspider.metier.responseParser.transformer.date.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspider.metier.responseParser.transformer.Transformer;

public class LBCDateTransformer implements Transformer {
	private static final Logger logger = LoggerFactory
			.getLogger(LBCDateTransformer.class);

	private Pattern todayPattern = Pattern
			.compile("Aujourd'hui (\\d{2}):(\\d{2})");
	private Pattern yesterdayPattern = Pattern
			.compile("Hier (\\d{2}):(\\d{2})");

	@Override
	public String transform(String arg) {
		DateFormat resultDf = new SimpleDateFormat("dd MMMMM hh:mm");
		DateFormat df = new SimpleDateFormat("dd MMMMM hh:mm");
		Date date = null;

		Matcher m = null;
		int hours = 0, minutes = 0;

		m = todayPattern.matcher(arg);
		if (m.matches()) {
			hours = Integer.parseInt(m.group(1));
			minutes = Integer.parseInt(m.group(2));
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, hours);
			c.set(Calendar.MINUTE, minutes);
			return resultDf.format(c.getTime());
		}

		m = yesterdayPattern.matcher(arg);
		if (m.matches()) {
			hours = Integer.parseInt(m.group(1));
			minutes = Integer.parseInt(m.group(2));
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -1);
			c.set(Calendar.HOUR_OF_DAY, hours);
			c.set(Calendar.MINUTE, minutes);
			return resultDf.format(c.getTime());
		}

		try {
			return (resultDf.format(df.parse(arg)));
		} catch (ParseException e) {
			logger.error("Date Parsing Error", e);
			return null;
		}

	}

	@Override
	public String transform(Element e) {
		return null;
	}
}
