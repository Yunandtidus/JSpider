package com.jspider.spider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.asyncLog.LoggerThread;
import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.ResultSearcher;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;
import com.jspider.model.urls.UrlLister;
import com.jspider.spider.configuration.SpiderConfiguration;

public class Spider implements InitializingBean {
	private final static Logger LOGGER = LoggerFactory.getLogger(Spider.class);
	private final static Logger THREAD_LOGGER = LoggerFactory.getLogger(Thread.class);

	/**
	 * Map url regex -> ResultSearcher
	 */
	private Map<String, ResultSearcher> mapResultSearcher;
	private Map<String, SpiderCallBack> mapSpiderCallback;

	private SpiderConfiguration spiderConf;

	private ExecutorService scheduler;

	private List<LoggerThread> loggers;

	private SpiderExchange spiderExchange;

	public void spider(UrlLister urlLister) throws ApplicationException {
		long lastUrlTime = 0;
		int nbTreatedUrls = 0;
		while (true) {
			while (!urlLister.isEmpty()
					&& (!isMaximumRequestedUrlReached(nbTreatedUrls))) {
				lastUrlTime = System.currentTimeMillis();
				Map.Entry<String, Integer> urlEntry = urlLister.getNext();
				nbTreatedUrls++;
				Runnable worker = new SpiderThread(urlEntry.getKey(), urlLister, urlEntry.getValue(), spiderExchange);
				scheduler.execute(worker);

			}
			try {
				if (urlLister.isEmpty()) {
					LOGGER.trace("No more urls to request");
				} else {
					LOGGER.debug("Maximum number of URLs requested (" + spiderConf.getMaximumTotalRequestedUrls() + ")");
				}
				Thread.sleep(50);
				if (shouldShutdown(urlLister, lastUrlTime, nbTreatedUrls)) {
					THREAD_LOGGER.info("Shutdown scheduler");
					scheduler.shutdownNow();
					return;
				}
			} catch (InterruptedException e) {
				THREAD_LOGGER.error("", e);
			}
		}
	}

	private boolean shouldShutdown(UrlLister urlLister, long lastUrlTime, int nbTreatedUrls) {
		if (isMaximumRequestedUrlReached(nbTreatedUrls)) {
			THREAD_LOGGER.debug("Should shutdown cause maximum requested Url reached("
					+ spiderConf.getMaximumTotalRequestedUrls() + ")");

			if (urlLister.urlProcessing()) {
				return false;
			} else {
				return true;
			}
		}
		if (urlLister.isEmpty()) {
			if (System.currentTimeMillis() - lastUrlTime > spiderConf.getTimeWithoutUrls()) {
				THREAD_LOGGER.debug("Should shutdown, waiting time ellapsed");
				return true;
			}
			if (urlLister.isDontWaitMore()) {
				THREAD_LOGGER.debug("Should shutdown cause no more urls and should not wait");
				if (urlLister.urlProcessing()) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isMaximumRequestedUrlReached(int nbRequest) {
		return spiderConf.getMaximumTotalRequestedUrls() > 0 && nbRequest >= spiderConf
				.getMaximumTotalRequestedUrls();
	}

	private class SpiderThread extends Thread {
		private String url;
		private UrlLister urlLister;
		private int urlIndex;
		private SpiderExchange exchange;

		public SpiderThread(String url, UrlLister urlLister, int urlIndex, SpiderExchange exchange) {
			this.url = url;
			this.urlLister = urlLister;
			this.urlIndex = urlIndex;
			this.exchange = exchange;
		}

		@Override
		public void run() {
			boolean shouldWait = true;
			try {
				THREAD_LOGGER.debug("Run SpiderThread " + getName());
				boolean match = false;
				for (String pattern : mapResultSearcher.keySet()) {
					if (url.matches(pattern)) {
						match = true;
						Search s = new Search(url);
						s.setUrlIndex(urlIndex);
						ResultsModel result;
						try {
							result = mapResultSearcher.get(pattern).search(s);
							if (result == null) {
								LOGGER.error("No result found for request : " + url);
							} else if (result.getError()) {
								LOGGER.error("Error " + result.getUrl() + " : " + result.getMessages());
							} else {
								shouldWait = !result.isCachedRequest();
								boolean callback = false;
								for (String patternCallback : mapSpiderCallback.keySet()) {
									if (url.matches(patternCallback)) {
										mapSpiderCallback.get(patternCallback).callback(result, exchange, urlLister);
										callback = true;
										break;
									}
								}
								if (!callback) {
									LOGGER.warn("no matching callback for url " + url);
								}
							}
						} catch (ApplicationException e) {
							LOGGER.error("", e);
							break;
						}
					}
				}

				if (!match) {
					LOGGER.warn("no matching result searcher for url " + url);
				}

				urlLister.processed(url);

				try {
					if (shouldWait) {
						Thread.sleep(spiderConf.getTimeBetweenSearchs());
					}
				} catch (InterruptedException e) {
				}

				THREAD_LOGGER.debug("End SpiderThread " + getName());
			} catch (Throwable t) {
				LOGGER.error("", t);
			}
		}
	}

	public Map<String, SpiderCallBack> getMapSpiderCallback() {
		return mapSpiderCallback;
	}

	public void setMapSpiderCallback(
			Map<String, SpiderCallBack> mapSpiderCallback) {
		this.mapSpiderCallback = mapSpiderCallback;
	}

	public Map<String, ResultSearcher> getMapResultSearcher() {
		return mapResultSearcher;
	}

	public void setMapResultSearcher(
			Map<String, ResultSearcher> mapResultSearcher) {
		this.mapResultSearcher = mapResultSearcher;
	}

	public SpiderConfiguration getSpiderConf() {
		return spiderConf;
	}

	public void setSpiderConf(SpiderConfiguration spiderConf) {
		this.spiderConf = spiderConf;
	}

	public List<LoggerThread> getLoggers() {
		return loggers;
	}

	public void setLoggers(List<LoggerThread> loggers) {
		this.loggers = loggers;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduler = Executors.newFixedThreadPool(spiderConf.getNbThreads());
	}

	public SpiderExchange getSpiderExchange() {
		return spiderExchange;
	}

	public void setSpiderExchange(SpiderExchange spiderExchange) {
		this.spiderExchange = spiderExchange;
	}

}
