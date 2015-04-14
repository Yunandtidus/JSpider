package com.jspider.metier.responseParser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.responseParser.ResponseParser;
import com.jspider.metier.responseParser.model.ParserConfiguration;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.utils.ListUtils;

public class GenericHtmlResponseParser implements ResponseParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericHtmlResponseParser.class);

	protected String encoding = "utf-8";
	protected String urlRelative = "";

	private List<ParserConfiguration> listConfiguration;
	private List<ParserConfiguration> elementValidator;
	private List<ParserConfiguration> nbResultsConfiguration;
	private List<ParserConfiguration> nextUrlConfiguration;
	private Map<String, List<ParserConfiguration>> parserConfiguration;

	@Override
	public ResultsModel parse(InputStream is, ResultsModel resultsModel)
			throws ApplicationException {
		Elements es = init(is, resultsModel);

		if (es == null) {
			return resultsModel;
		}
		LOGGER.debug(es.size() + " elements");
		for (Element e : es) {
			if (isValid(e)) {

				Map<String, String> result = new HashMap<String, String>();
				for (Entry<String, List<ParserConfiguration>> entry : parserConfiguration
						.entrySet()) {
					String val = parseElement(e, entry.getValue());
					LOGGER.debug(entry.getKey() + " > " + val);
					result.put(entry.getKey(), val);
				}

				resultsModel.getResults().add(result);
			}
		}
		return resultsModel;

	}

	protected boolean isValid(Element e) {
		return elementValidator == null
				|| parseElement(e, elementValidator) != null;
	}

	protected Elements init(InputStream is, ResultsModel resultsModel)
			throws ApplicationException {
		Document document;
		try {
			document = getDocument(is);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		if (nbResultsConfiguration != null) {
			resultsModel.setTotal(getNbResults(document));
		}

		if (nextUrlConfiguration != null) {
			resultsModel.setNextUrl(getNextUrl(document));
		}

		return getListElements(document);
	}

	public List<ParserConfiguration> getNextUrlConfiguration() {
		return nextUrlConfiguration;
	}

	public void setNextUrlConfiguration(
			List<ParserConfiguration> nextUrlConfiguration) {
		this.nextUrlConfiguration = nextUrlConfiguration;
	}

	protected Elements getListElements(Element element) {
		Elements es = null;

		if (!ListUtils.nullOrEmpty(listConfiguration)) {
			es = listConfiguration.get(0).parseToElements(element);
			for (int i = 1; i < listConfiguration.size(); i++) {
				es = listConfiguration.get(i).parseToElements(es);
			}
		}
		return es;

	}

	protected int getNbResults(Element element) {
		return Integer.parseInt(parseElement(element, nbResultsConfiguration));
	}

	protected String getNextUrl(Element element) {
		return parseElement(element, nextUrlConfiguration);
	}

	private String parseElement(Element e, List<ParserConfiguration> conf) {
		Element curr = e;
		int i = 0;
		for (i = 0; i < conf.size() - 1 && curr != null; i++) {
			curr = conf.get(i).parseToElement(curr);
		}
		return curr == null ? null : conf.get(i).parse(curr);
	}

	protected Document getDocument(InputStream is) throws IOException {
		return Jsoup.parse(is, encoding, urlRelative);
	}

	public Map<String, List<ParserConfiguration>> getParserConfiguration() {
		return parserConfiguration;
	}

	public void setParserConfiguration(
			Map<String, List<ParserConfiguration>> parserConfiguration) {
		this.parserConfiguration = parserConfiguration;
	}

	public List<ParserConfiguration> getListConfiguration() {
		return listConfiguration;
	}

	public void setListConfiguration(List<ParserConfiguration> listConfiguration) {
		this.listConfiguration = listConfiguration;
	}

	public List<ParserConfiguration> getNbResultsConfiguration() {
		return nbResultsConfiguration;
	}

	public void setNbResultsConfiguration(
			List<ParserConfiguration> nbResultsConfiguration) {
		this.nbResultsConfiguration = nbResultsConfiguration;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getUrlRelative() {
		return urlRelative;
	}

	public void setUrlRelative(String urlRelative) {
		this.urlRelative = urlRelative;
	}

	public List<ParserConfiguration> getElementValidator() {
		return elementValidator;
	}

	public void setElementValidator(List<ParserConfiguration> elementValidator) {
		this.elementValidator = elementValidator;
	}

}
