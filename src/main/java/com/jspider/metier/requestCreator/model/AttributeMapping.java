package com.jspider.metier.requestCreator.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jspider.metier.responseParser.transformer.Transformer;

public class AttributeMapping {
	private RequestAction action;
	private String value;
	private boolean needConversion;
	private String parameters;
	private String defaultValue;
	private List<String> parametersList;
	private Transformer transformer;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttributeMapping [action="
				+ action
				+ ", value="
				+ value
				+ ", parameters("
				+ (getParametersList() == null ? "" : getParametersList()
						.size()) + ")=" + getParametersList() + "]";
	}

	public RequestAction getAction() {
		return action;
	}

	public void setAction(RequestAction action) {
		this.action = action;
	}

	public List<String> getParametersList() {
		if (parametersList == null) {
			if (parameters == null) {
				parametersList = Collections.emptyList();
			} else {
				parametersList = Arrays.asList(parameters.split(","));
			}
		}
		return parametersList;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public boolean isNeedConversion() {
		return needConversion;
	}

	public void setNeedConversion(boolean needConversion) {
		this.needConversion = needConversion;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

}
