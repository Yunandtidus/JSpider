package com.jspider.spider;

import java.util.Collection;

import com.jspider.metier.resultSearcher.model.ResultsModel;

public interface SpiderCallBack {
	/**
	 * Callback
	 */
	public void callback(ResultsModel rm, Collection<String> urls);
}
