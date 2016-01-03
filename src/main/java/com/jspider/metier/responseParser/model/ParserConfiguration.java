package com.jspider.metier.responseParser.model;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jspider.metier.responseParser.transformer.Transformer;

public class ParserConfiguration {
	private ElementParserTools elementParser;
	private String value;
	private String regex;
	private String dateFormat;

	private String messageFormat;
	private Locale locale = Locale.getDefault();
	private int index = 0;
	private boolean spaceDeletion = false;
	private boolean urlDecode = false;
	private Transformer transformer;

	public Object parse(Element e) {
		String val = null;
		switch (this.elementParser) {
		case ELEMENT:
			return e;
		case CONSTANT:
			val = this.value;
			break;

		case GET_ATTR:
			val = e.attr(this.value);
			break;

		case VAL_COMPUTING:
			val = e.text();
			val = this.transformer.transform(val);
			break;

		case ELEMENT_COMPUTING:
			val = this.transformer.transform(e);
			break;

		case GET_VAL:
			val = e.text();
			break;

		case GET_CHILDREN_VALS:
			val = "";
			for (Element child : e.children()) {
				val += child.text() + "/";
			}
			val = val.substring(0, val.length() - 1);
			break;

		case GET_DATE:
			val = e.text();
			try {
				Date d = new SimpleDateFormat(dateFormat, locale).parse(val);
				val = new SimpleDateFormat("dd/MM/yyyy").format(d);
			} catch (ParseException e2) {
				e2.printStackTrace();
			}

		default:
		}

		if (regex != null) {
			Pattern p = Pattern.compile(this.regex);
			Matcher m = p.matcher(val);
			if (m.find()) {
				val = m.group(1);
			} else {
				val = null;
			}
		}

		if (isSpaceDeletion() && val != null) {
			val = val.replaceAll("[ \\s]", "");
		}
		if (this.urlDecode) {
			try {
				val = java.net.URLDecoder.decode(val, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		if (this.messageFormat != null) {
			val = MessageFormat.format(messageFormat, val);
		}
		return val;

	}

	public Element parseToElement(Element e) {
		Elements es = null;
		switch (this.elementParser) {
		case GET_CHILDREN:
			es = e.children();
			break;
		case GET_ELEMENT_BY_TAG:
			es = e.getElementsByTag(this.value);
			break;
		case GET_ELEMENT_BY_CLASS:
			es = e.getElementsByClass(this.value);
			break;
		case GET_ELEMENT_BY_ID:
			return e.getElementById(this.value);
		case GET_ELEMENT_BY_SELECTOR:
			es = e.select(this.value);
			break;
		default:
			return null;
		}

		if (es == null || es.isEmpty()) {
			return null;
		}

		if (this.index >= 0) {
			return es.get(index);
		}

		return es.get(es.size() + index);
	}

	public Elements parseToElements(Element e) {
		Elements es;
		switch (this.elementParser) {
		case GET_CHILDREN:
			es = e.children();
			break;
		case GET_ELEMENT_BY_TAG:
			es = e.getElementsByTag(this.value);
			break;
		case GET_ELEMENT_BY_CLASS:
			es = e.getElementsByClass(this.value);
			break;
		case GET_ELEMENT_BY_SELECTOR:
			es = e.select(this.value);
			break;
		case GET_ELEMENT_BY_ID:
			Elements elems = new Elements();
			elems.add(e.getElementById(this.value));
			es = elems;
			break;
		default:
			return null;
		}

		if (index > 0) {
			return new Elements(es.subList(index, es.size()));
		}
		return es;
	}

	public Elements parseToElements(Elements es) {
		Elements elems;
		switch (this.elementParser) {
		case GET_CHILDREN:
			elems = es.get(0).children();
			break;
		case GET_ELEMENT_BY_TAG:
			elems = es.get(0).getElementsByTag(this.value);
			break;
		case GET_ELEMENT_BY_CLASS:
			elems = es.get(0).getElementsByClass(this.value);
			break;
		case GET_ELEMENT_BY_ID:
			elems = new Elements();
			elems.add(es.get(0).getElementById(this.value));
			break;
		default:
			return null;
		}

		if (index > 0) {
			return new Elements(elems.subList(index, elems.size()));
		}
		return elems;
	}

	public JSONObject parseToJSONObject(JSONObject o) {
		switch (this.elementParser) {
		case GET_JSONOBJECT_BY_NAME:
			return o.getJSONObject(this.value);
		default:
			return null;
		}
	}

	public String parse(JSONObject o) {
		String val = null;
		switch (this.elementParser) {
		case GET_STRING_BY_NAME:
			val = o.getString(this.value);
			break;
		case GET_JSON_INT_BY_NAME:
			val = Integer.toString(o.getInt(this.value));
			break;

		case CONSTANT:
			val = this.value;
			break;
		default:
			return null;
		}

		if (this.messageFormat != null) {
			val = MessageFormat.format(messageFormat, val);
		}
		return val;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public boolean isSpaceDeletion() {
		return spaceDeletion;
	}

	public void setSpaceDeletion(boolean spaceDeletion) {
		this.spaceDeletion = spaceDeletion;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean isUrlDecode() {
		return urlDecode;
	}

	public void setUrlDecode(boolean urlDecode) {
		this.urlDecode = urlDecode;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ElementParserTools getElementParser() {
		return elementParser;
	}

	public void setElementParser(ElementParserTools elementParser) {
		this.elementParser = elementParser;
	}
}
