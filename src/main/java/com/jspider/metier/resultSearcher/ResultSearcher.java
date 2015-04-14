package com.jspider.metier.resultSearcher;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;

public interface ResultSearcher {
	public ResultsModel search(Search search) throws ApplicationException;
}
