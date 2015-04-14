package com.jspider.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Search generated by hbm2java
 */
public class Search implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String url;
	private Map<String, String> criteria = new HashMap<String, String>();

	public Search() {
	}

	public Search(String url) {
		this.url = url;
	}

	public Map<String, String> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
