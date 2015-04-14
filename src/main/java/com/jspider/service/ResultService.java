package com.jspider.service;

import java.util.List;

import com.jspider.exception.ApplicationException;
import com.jspider.model.Result;
import com.jspider.model.Search;

public interface ResultService {
	List<Result> getBySearchId(Integer searchId);

	List<Result> searchResults(Search search) throws ApplicationException;
}
