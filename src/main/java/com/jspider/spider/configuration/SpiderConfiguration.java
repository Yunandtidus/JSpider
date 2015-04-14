package com.jspider.spider.configuration;

public class SpiderConfiguration {
	public enum Conf {
		REFRESH, INIT
	}

	private Conf conf;

	public Conf getConf() {
		return conf;
	}

	public void setConf(Conf conf) {
		this.conf = conf;
	}
}
