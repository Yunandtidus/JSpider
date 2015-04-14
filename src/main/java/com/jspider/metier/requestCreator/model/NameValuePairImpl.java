package com.jspider.metier.requestCreator.model;

import org.apache.http.NameValuePair;

public class NameValuePairImpl implements NameValuePair {

	private String name;
	private String value;

	public NameValuePairImpl(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{" + name + ":" + value + "}";
	}
}
