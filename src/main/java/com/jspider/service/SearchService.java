package com.jspider.service;

import java.util.List;

import com.jspider.model.Search;

public interface SearchService {
	List<Search> getAll();
	Search getById(Integer id);
	
	Search create(Search search);
	Search update(Search search);
	void delete(Search search);
	void deleteById(Integer searchId);
}
