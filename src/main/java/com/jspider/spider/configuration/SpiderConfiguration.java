package com.jspider.spider.configuration;

public class SpiderConfiguration {
	public enum Conf {
		REFRESH, INIT
	}

	/**
	 * 
	 */
	private int nbThreads = 5;
	/**
	 * 
	 */
	private int timeBetweenSearchs = 100;

	/**
	 * 
	 */
	private int timeWithoutUrls = 10000;

	/***
	 * 
	 */
	private int schedulerTerminationWaitingTime = 5000;

	/**
	 * 
	 */
	private int maximumTotalRequestedUrls = 1000;

	/**
	 * @return the timeBetweenSearchs
	 */
	public int getTimeBetweenSearchs() {
		return timeBetweenSearchs;
	}

	/**
	 * @param timeBetweenSearchs
	 *            the timeBetweenSearchs to set
	 */
	public void setTimeBetweenSearchs(int timeBetweenSearchs) {
		this.timeBetweenSearchs = timeBetweenSearchs;
	}

	private Conf conf;

	public Conf getConf() {
		return conf;
	}

	public void setConf(Conf conf) {
		this.conf = conf;
	}

	public int getNbThreads() {
		return nbThreads;
	}

	public void setNbThreads(int nbThreads) {
		this.nbThreads = nbThreads;
	}

	public int getSchedulerTerminationWaitingTime() {
		return schedulerTerminationWaitingTime;
	}

	public void setSchedulerTerminationWaitingTime(int schedulerTerminationWaitingTime) {
		this.schedulerTerminationWaitingTime = schedulerTerminationWaitingTime;
	}

	public int getMaximumTotalRequestedUrls() {
		return maximumTotalRequestedUrls;
	}

	public void setMaximumTotalRequestedUrls(int maximumTotalRequestedUrls) {
		this.maximumTotalRequestedUrls = maximumTotalRequestedUrls;
	}

	public int getTimeWithoutUrls() {
		return timeWithoutUrls;
	}

	public void setTimeWithoutUrls(int timeWithoutUrls) {
		this.timeWithoutUrls = timeWithoutUrls;
	}
}
