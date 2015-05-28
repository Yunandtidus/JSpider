package com.jspider.metier.resultSearcher.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.ConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.requestCreator.RequestCreator;
import com.jspider.metier.responseParser.ResponseParser;
import com.jspider.metier.resultSearcher.ResultSearcher;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;

public class GenericResultSearcher implements ResultSearcher, InitializingBean {

	private static Logger LOG = LoggerFactory.getLogger(GenericResultSearcher.class);

	private ResponseParser responseParser;
	private RequestCreator requestCreator;

	private String saveResponseFolderName = null;
	private File saveResponseFolder = null;

	public InputStream getStream(Search search, ResultsModel resultsModel)
			throws ApplicationException {

		return requestCreator.request(resultsModel, search);

	}

	public ResultsModel convert(InputStream stream, ResultsModel resultsModel)
			throws ApplicationException {
		return responseParser.parse(stream, resultsModel);
	}

	@Override
	public ResultsModel search(Search search) {
		ResultsModel resultsModel = new ResultsModel();
		try {
			LOG.debug("Request " + search.getUrl());
			InputStream is = getCachedResponse(search);
			if (is == null) {
				long time = System.currentTimeMillis();
				is = getStream(search, resultsModel);
				LOG.debug("HTTP Request (" + (System.currentTimeMillis() - time) + "ms) " + search.getUrl());
			} else {
				LOG.debug("Cached Request " + search.getUrl());
			}
			if (is == null) {
				return resultsModel;
			}

			is = getResetableStream(is);

			logRequest(search, is);

			return convert(is, resultsModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private InputStream getCachedResponse(Search search) throws FileNotFoundException {
		if (saveResponseFolder != null) {
			File cachedResponse = getResponseFile(search);
			if (cachedResponse.exists() && !cachedResponse.isDirectory()) {
				return new FileInputStream(cachedResponse);
			}
		}
		return null;
	}

	private File getResponseFile(Search search) {
		return new File(saveResponseFolder, getRequestIdentifier(search));
	}

	private void logRequest(Search search, InputStream is) throws IOException {
		if (saveResponseFolder != null) {
			OutputStream fos = new FileOutputStream(getResponseFile(search));
			IOUtils.copy(is, fos);

			is.reset();
		}
	}

	private String getRequestIdentifier(Search search) {
		String fileName = search.getUrl();
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		return "p_" + fileName;

	}

	private InputStream getResetableStream(InputStream is) throws IOException {
		if (is.markSupported()) {
			return is;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		byte[] bytes = baos.toByteArray();

		return new ByteArrayInputStream(bytes);
	}

	public RequestCreator getRequestCreator() {
		return requestCreator;
	}

	public void setRequestCreator(RequestCreator requestCreator) {
		this.requestCreator = requestCreator;
	}

	public ResponseParser getResponseParser() {
		return responseParser;
	}

	public void setResponseParser(ResponseParser responseParser) {
		this.responseParser = responseParser;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (saveResponseFolderName != null) {
			saveResponseFolder = new File(saveResponseFolderName);
			if (saveResponseFolder.exists() && !saveResponseFolder.isDirectory()) {
				throw new ConfigurationException();
			}

			saveResponseFolder.mkdir();
		}
	}

	public String getSaveResponseFolderName() {
		return saveResponseFolderName;
	}

	public void setSaveResponseFolderName(String saveResponseFolderName) {
		this.saveResponseFolderName = saveResponseFolderName;
	}

}