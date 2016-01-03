package com.jspider.metier.requestCreator.impl;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.requestCreator.RequestCreator;
import com.jspider.metier.requestCreator.model.AttributeMapping;
import com.jspider.metier.requestCreator.model.NameValuePairImpl;
import com.jspider.metier.requestCreator.model.RequestContainer;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.metier.utils.TpmConstants;
import com.jspider.model.Search;

public class GenericRequestCreator implements RequestCreator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericRequestCreator.class);

	private Map<String, AttributeMapping> attributeMappings;
	private String baseUrl;
	private ContentType postContentType = ContentType.DEFAULT_TEXT;

	private Properties modelProperties = new Properties();

	@Override
	public InputStream request(ResultsModel resultsModel, Search search)
			throws ApplicationException {
		HttpResponse httpResponse = null;
		try {
			RequestContainer requestContainer = new RequestContainer();
			String url = baseUrl;
			if (search.getUrl() != null) {
				url = search.getUrl();
			}
			requestContainer.setUrl(url);

			resultsModel.setUrl(url);

			applyAttributes(requestContainer, search);

			url = requestContainer.getUrl();
			if (!requestContainer.getParametersGET().isEmpty()) {
				url += "?";
				for (Map.Entry<String, String> entry : requestContainer
						.getParametersGET().entrySet()) {
					url += entry.getKey() + "=" + entry.getValue() + "&";
				}
			}

			Request request = null;
			if (!requestContainer.getParametersPOST().isEmpty()) {
				LOGGER.debug("POST url : " + url + " : "
						+ requestContainer.getParametersPOST());
				request = post(requestContainer, url);
			} else {
				LOGGER.debug("GET url : " + url);
				request = Request.Get(url);
			}

			if (search.getReferer() != null) {
				request.setHeader("Referer", search.getReferer());
			}

			Response response = request.execute();
			httpResponse = response.returnResponse();
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				resultsModel.setError(true);
				resultsModel.setMessages(Arrays.asList("Error "
						+ httpResponse.getStatusLine().getStatusCode()));
				return null;
			}
			System.out.println(Arrays.asList(httpResponse.getHeaders("Content-Disposition")));

			return httpResponse.getEntity().getContent();
		} catch (Exception e) {
			LOGGER.error("", e);
			resultsModel.setError(true);
			resultsModel.getMessages().add(TpmConstants.TECHNICAL_ERROR);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void applyAttributes(RequestContainer requestContainer, Search search) {
		if (search == null || search.getCriteria() == null
				|| attributeMappings == null) {
			return;
		}
		Map<String, String> requestParams = search.getCriteria();
		for (Entry<String, AttributeMapping> entryAttributeMapping : attributeMappings
				.entrySet()) {

			String nameAttr = entryAttributeMapping.getKey();
			AttributeMapping am = entryAttributeMapping.getValue();

			String value = null;
			if (requestParams.containsKey(nameAttr)
					&& requestParams.get(nameAttr) != null) {
				value = requestParams.get(nameAttr);
				if (am.isNeedConversion()) {
					value = modelProperties.getProperty(value);
				} else if (am.getTransformer() != null) {
					value = am.getTransformer().transform(value);
				}
			} else {
				if (am.getDefaultValue() != null) {
					value = am.getDefaultValue();
				}
			}
			if (value != null) {
				switch (am.getAction()) {
				case POST_PARAMETER:
					Map<String, Object> currLvl = requestContainer
							.getParametersPOST();
					String[] subValues = am.getValue().split("\\.");
					String subValue = am.getValue();
					for (int i = 0; i < subValues.length - 1; i++) {
						subValue = subValues[i];
						if (!currLvl.containsKey(subValue)) {
							currLvl.put(subValue, new HashMap<String, Object>());
						}
						currLvl = (Map<String, Object>) currLvl.get(subValue);
					}
					if (subValues.length > 0) {
						subValue = subValues[subValues.length - 1];
					}
					currLvl.put(subValue, value);

					break;
				case GET_PARAMETER:
					requestContainer.getParametersGET().put(am.getValue(),
							value);
					break;
				case URL_EXTENSION:
					int index = Integer.parseInt(am.getParametersList().get(0));
					requestContainer.setUrl(requestContainer.getUrl().replace(
							"#" + index, value));
					break;
				default:
					throw new InvalidParameterException(am.getAction()
							.toString());
				}
			}

		}

	}

	private Request post(RequestContainer rc, String url) {

		if (ContentType.DEFAULT_TEXT.equals(postContentType)) {
			NameValuePair[] nvPairs = generateNVPairs(rc.getParametersPOST());
			return Request.Post(url).bodyForm(nvPairs);
		} else if (ContentType.APPLICATION_JSON.equals(postContentType)) {
			return Request.Post(url).bodyString(
					rc.getParametersPOST().toString(),
					ContentType.APPLICATION_JSON);
		}
		return null;
	}

	private NameValuePair[] generateNVPairs(Map<String, Object> map) {
		System.out.println("generate" + map);
		NameValuePair[] nvPairs = new NameValuePair[map.size()];
		int count = 0;
		for (Entry<String, Object> entry : map.entrySet()) {
			nvPairs[count] = new NameValuePairImpl(entry.getKey(),
					(String) entry.getValue());
			count++;
		}
		return nvPairs;
	}

	public Map<String, AttributeMapping> getAttributeMappings() {
		return attributeMappings;
	}

	public void setAttributeMappings(
			Map<String, AttributeMapping> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Properties getModelProperties() {
		return modelProperties;
	}

	public void setModelProperties(Properties modelProperties) {
		this.modelProperties = modelProperties;
	}

	public ContentType getPostContentType() {
		return postContentType;
	}

	public void setPostContentType(ContentType postContentType) {
		this.postContentType = postContentType;
	}

}
