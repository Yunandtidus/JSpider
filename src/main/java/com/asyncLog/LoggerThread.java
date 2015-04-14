package com.asyncLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncLog.service.AsyncLogService;

public class LoggerThread extends Thread {
	private static final Logger logger = LoggerFactory
			.getLogger(LoggerThread.class);

	private int ms;
	private AsyncLogService logService;

	private String name;
	private String filePrefix = "";

	@Override
	public void run() {
		try {
			System.out.println("making a log");
			logService.logCounter(name, filePrefix);
		} catch (Throwable e) {
			logger.error("", e);
		}
	}

	public int getMs() {
		return ms;
	}

	public void setMs(int ms) {
		this.ms = ms;
	}

	public AsyncLogService getLogService() {
		return logService;
	}

	public void setLogService(AsyncLogService logService) {
		this.logService = logService;
	}

	public String getCounterName() {
		return name;
	}

	public void setCounterName(String name) {
		this.name = name;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
}
