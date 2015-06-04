package com.jspider.spider;

import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.urls.UrlLister;

public interface SpiderCallBack {
	/**
	 * Callback
	 */
	public void callback(ResultsModel rm, UrlLister urlLister);
}
