package com.jspider.metier.requestCreator;

import java.io.InputStream;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;

public interface RequestCreator {
	/**
	 * 
	 * @param args
	 * @return the inputStream containing request result
	 */
	public InputStream request(ResultsModel resultsModel, Search search)
			throws ApplicationException;
}
