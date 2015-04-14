package com.asyncLog.service;

public interface AsyncLogService {

	public void incrCounter(String name);

	public void logCounter(String name, String filePrefix);

}
