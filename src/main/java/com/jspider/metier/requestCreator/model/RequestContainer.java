package com.jspider.metier.requestCreator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RequestContainer {
	private String url;
	private SortedMap<Integer, String> urlExtensions = new TreeMap<Integer, String>();
	private Map<String, String> parametersGET = new HashMap<String, String>();
	private Map<String, Object> parametersPOST = new HashMap<String, Object>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParametersGET() {
		return parametersGET;
	}

	public void setParametersGET(Map<String, String> parametersGET) {
		this.parametersGET = parametersGET;
	}

	public Map<String, Object> getParametersPOST() {
		return parametersPOST;
	}

	public void setParametersPOST(Map<String, Object> parametersPOST) {
		this.parametersPOST = parametersPOST;
	}

	public SortedMap<Integer, String> getUrlExtensions() {
		return urlExtensions;
	}

	public void setUrlExtensions(SortedMap<Integer, String> urlExtensions) {
		this.urlExtensions = urlExtensions;
	}
}
