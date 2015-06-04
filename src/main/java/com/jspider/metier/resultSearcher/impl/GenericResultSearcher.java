package com.jspider.metier.resultSearcher.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
			LOG.debug("Request " + search.getUrl());
			is = getCachedResponse(search);
			if (is == null) {
				long time = System.currentTimeMillis();
				is = getStream(search, resultsModel);
				LOG.debug("HTTP Request (" + (System.currentTimeMillis() - time) + "ms) " + search.getUrl());
				is = getResetableStream(is);
				saveRequest(search, is);
			} else {
				is = getResetableStream(is);
				LOG.debug("Cached Request " + search.getUrl());
			}
			if (is == null) {
				LOG.warn("Can't find InputStream");
				return resultsModel;
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
				ZipFile zip = new ZipFile(cachedResponse);
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					System.out.println(entry.getName());
					return zip.getInputStream(entry);
				}
			}
		}
		return null;
	}

	private File getResponseFile(Search search) {
		return new File(saveResponseFolder, getRequestIdentifier(search));
	}

	private void saveRequest(Search search, InputStream is) throws IOException {
		if (saveResponseFolder != null) {
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
		return "z" + search.getUrlIndex() + "_" + fileName;

	}

	private InputStream getResetableStream(InputStream is) throws IOException {
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