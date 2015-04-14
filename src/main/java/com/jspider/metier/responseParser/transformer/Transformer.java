package com.jspider.metier.responseParser.transformer;

import org.jsoup.nodes.Element;

public interface Transformer {
	String transform(String arg);

	String transform(Element e);
}
