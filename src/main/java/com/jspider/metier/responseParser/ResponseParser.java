package com.jspider.metier.responseParser;

import java.io.InputStream;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.model.ResultsModel;

public interface ResponseParser {

	public ResultsModel parse(InputStream is, ResultsModel resultsModel)
			throws ApplicationException;
}
