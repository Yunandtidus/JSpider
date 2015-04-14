package com.jspider.metier.resultSearcher.impl;

import java.io.InputStream;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.requestCreator.RequestCreator;
import com.jspider.metier.responseParser.ResponseParser;
import com.jspider.metier.resultSearcher.ResultSearcher;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;

public class GenericResultSearcher implements ResultSearcher {

	private ResponseParser responseParser;
	private RequestCreator requestCreator;

	public InputStream getStream(Search search, ResultsModel resultsModel)
			throws ApplicationException {

		return requestCreator.request(resultsModel, search);

	}

	public ResultsModel convert(InputStream stream, ResultsModel resultsModel)
			throws ApplicationException {
		return responseParser.parse(stream, resultsModel);
	}

	@Override
	public ResultsModel search(Search search) {
		ResultsModel resultsModel = new ResultsModel();
		try {
			InputStream is = getStream(search, resultsModel);
			if (is == null) {
				return resultsModel;
			}
			return convert(is, resultsModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public RequestCreator getRequestCreator() {
		return requestCreator;
	}

	public void setRequestCreator(RequestCreator requestCreator) {
		this.requestCreator = requestCreator;
	}

	public ResponseParser getResponseParser() {
		return responseParser;
	}

	public void setResponseParser(ResponseParser responseParser) {
		this.responseParser = responseParser;
	}
}