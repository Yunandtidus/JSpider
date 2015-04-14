package com.jspider.spider.impl;

import com.asyncLog.service.AsyncLogService;
import com.jspider.spider.SpiderCallBack;

public abstract class AbstractSpiderCallBack implements SpiderCallBack {
	protected AsyncLogService logService;

	public AsyncLogService getLogService() {
		return logService;
	}

	public void setLogService(AsyncLogService logService) {
		this.logService = logService;
	}
}
