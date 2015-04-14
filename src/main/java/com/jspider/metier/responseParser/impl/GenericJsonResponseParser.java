package com.jspider.metier.responseParser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.responseParser.ResponseParser;
import com.jspider.metier.responseParser.model.ParserConfiguration;
import com.jspider.metier.resultSearcher.model.ResultsModel;

public class GenericJsonResponseParser implements ResponseParser {

	private List<ParserConfiguration> listConfiguration;
	private List<ParserConfiguration> nbResultsConfiguration;
	private Map<String, List<ParserConfiguration>> parserConfiguration;

	@Override
	public ResultsModel parse(InputStream is, ResultsModel resultsModel) throws ApplicationException {
		try {
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null) {
				responseStrBuilder.append(inputStr);
			}
			JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

			if (nbResultsConfiguration != null) {
				resultsModel.setTotal(Integer.parseInt(parseJsonObject(jsonObject, nbResultsConfiguration)));
			}

			JSONArray items = jsonObject.getJSONArray("items");

			for (int i = 0; i < items.length(); i++) {

				Map<String, String> result = new HashMap<String, String>();
				JSONObject item = items.getJSONObject(i);
				System.out.println("---");
				for (Entry<String, List<ParserConfiguration>> entry : parserConfiguration.entrySet()) {
					String val = parseJsonObject(item, entry.getValue());
					System.out.println(entry.getKey() + " > " + val);
					result.put(entry.getKey(), val);
				}

				resultsModel.getResults().add(result);
			}

			System.out.println(Arrays.asList(JSONObject.getNames(jsonObject)));
			return resultsModel;
			// Document document = getDocument(is);
			//
			// Elements es = null;
			// // TODO null checking
			// es = listConfiguration.get(0).parseToElements(document);
			// for (int i = 1; i < listConfiguration.size(); i++) {
			// es = listConfiguration.get(i).parseToElements(es);
			// }
			// for (Element e : es) {
			// Map<String, String> result = new HashMap<String, String>();
			// System.out.println("---");
			// for (Entry<String, List<ParserConfiguration>> entry :
			// parserConfiguration.entrySet()) {
			// String val = parseElement(e, entry.getValue());
			// System.out.println(entry.getKey() + " > " + val);
			// result.put(entry.getKey(), val);
			// }
			//
			// resultsModel.getResults().add(result);
			// }
			// return resultsModel;
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

	}

	private String parseJsonObject(JSONObject o, List<ParserConfiguration> conf) {
		JSONObject curr = o;
		int i = 0;
		for (i = 0; i < conf.size() - 1 && curr != null; i++) {
			curr = conf.get(i).parseToJSONObject(curr);
		}
		return curr == null ? null : conf.get(i).parse(curr);
	}

	public Map<String, List<ParserConfiguration>> getParserConfiguration() {
		return parserConfiguration;
	}

	public void setParserConfiguration(Map<String, List<ParserConfiguration>> parserConfiguration) {
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

	public void setNbResultsConfiguration(List<ParserConfiguration> nbResultsConfiguration) {
		this.nbResultsConfiguration = nbResultsConfiguration;
	}
}
