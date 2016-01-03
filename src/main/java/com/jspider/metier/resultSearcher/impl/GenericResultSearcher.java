package com.jspider.metier.resultSearcher.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import com.jspider.utils.FileUtils;

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

	@SuppressWarnings("resource")
	@Override
	public ResultsModel search(Search search) {
		ResultsModel resultsModel = new ResultsModel();
		InputStream is = null;
		try {
			LOG.info("Request " + search.getUrl());
			is = getCachedResponse(search);
			if (is == null) {
				long time = System.currentTimeMillis();
				is = getStream(search, resultsModel);
				LOG.info("HTTP Request (" + (System.currentTimeMillis() - time) + "ms) " + search.getUrl());
				is = getResetableStream(is);
				saveRequest(search, is);
			} else {
				resultsModel.setCachedRequest(true);
				is = getResetableStream(is);
				LOG.info("Cached Request " + search.getUrl());
			}
			if (is == null) {
				LOG.warn("Can't find InputStream");
				return resultsModel;
			}

			if (LOG.isTraceEnabled()) {
				IOUtils.copy(is, System.out);
				is.reset();
			}

			return convert(is, resultsModel);
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}

	private InputStream getCachedResponse(Search search) throws IOException {
		if (saveResponseFolder != null) {
			File cachedResponse = getResponseFile(search);
			if (cachedResponse.exists() && !cachedResponse.isDirectory()) {
				return FileUtils.getFromZipFile(cachedResponse);
			}
		}
		return null;
	}

	private File getResponseFile(Search search) {
		return new File(saveResponseFolder, getRequestIdentifier(search));
	}

	private void saveRequest(Search search, InputStream is) throws IOException {
		if (saveResponseFolder != null && is != null) {
			final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getResponseFile(search)));
			ZipEntry e = new ZipEntry("page.html");
			out.putNextEntry(e);
			IOUtils.copy(is, out);

			out.closeEntry();
			out.close();

			is.reset();
		}
	}

	private String getRequestIdentifier(Search search) {
		String fileName = search.getUrl();
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		fileName = fileName.replace("?", "%3F");
		return "z_" + fileName;

	}

	private InputStream getResetableStream(InputStream is) throws IOException {
		if (is == null) {
			return null;
		}
		if (is.markSupported()) {
			return is;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		is.close();
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