package com.jspider.metier.resultSearcher.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultsModel {

	private String url;
	private boolean error;
	private List<String> messages = new ArrayList<String>();
	private int total;
	private List<String> nextUrls;
	private List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();;

	private boolean cachedRequest;

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}

	public List<String> getNextUrls() {
		return nextUrls;
	}

	public void setNextUrls(List<String> nextUrls) {
		this.nextUrls = nextUrls;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		if (error) {
			return "Error : " + messages;
		}
		return results.toString();
	}

	public boolean isCachedRequest() {
		return cachedRequest;
	}

	public void setCachedRequest(boolean cachedRequest) {
		this.cachedRequest = cachedRequest;
	}
}
