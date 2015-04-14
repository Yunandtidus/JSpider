/*
 * Creation : 25 août 2014
 */
package com.jspider.metier.responseParser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.model.ResultsModel;

public class GenericHtmlJsonResponseParser extends GenericHtmlResponseParser {

	private String nbResultsJson;
	private String htmlJson;

	@Override
	protected Elements init(InputStream is, ResultsModel resultsModel) throws ApplicationException {
		BufferedReader streamReader;
		StringBuilder responseStrBuilder;
		try {
			streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null) {
				responseStrBuilder.append(inputStr);
			}
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

		resultsModel.setTotal(jsonObject.getInt(nbResultsJson));

		Document document = Jsoup.parseBodyFragment(jsonObject.getString(htmlJson));

		return getListElements(document);
	}

	public String getNbResultsJson() {
		return nbResultsJson;
	}

	public void setNbResultsJson(String nbResultsJson) {
		this.nbResultsJson = nbResultsJson;
	}

	public String getHtmlJson() {
		return htmlJson;
	}

	public void setHtmlJson(String htmlJson) {
		this.htmlJson = htmlJson;
	}
}
