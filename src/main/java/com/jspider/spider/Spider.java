package com.jspider.spider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	private ScheduledExecutorService scheduler;

	private List<LoggerThread> loggers;

	public void spider(UrlLister urlLister) throws ApplicationException {
		if (loggers != null) {
			LOGGER.debug("Instantiating loggerThreads");
			for (LoggerThread t : loggers) {
				scheduler.scheduleAtFixedRate(t, t.getMs(), t.getMs(),
						TimeUnit.MILLISECONDS);
			}
		}
		long lastUrlTime = 0;
		int nbTreatedUrls = 0;
		while (true) {
			while (!urlLister.isEmpty()
					&& (!isMaximumRequestedUrlReached(nbTreatedUrls))) {
				lastUrlTime = System.currentTimeMillis();
				Map.Entry<String, Integer> urlEntry = urlLister.getNext();
				nbTreatedUrls++;
				Runnable worker = new SpiderThread(urlEntry.getKey(), urlLister, urlEntry.getValue());
				scheduler.execute(worker);

			}
			try {
				if (urlLister.isEmpty()) {
					LOGGER.trace("No more urls to request");
				} else {
					LOGGER.debug("Maximum number of URLs requested (" + spiderConf.getMaximumTotalRequestedUrls() + ")");
				}
				Thread.sleep(50);
				if (urlLister.isEmpty() && System.currentTimeMillis() - lastUrlTime > spiderConf.getTimeWithoutUrls()
						|| isMaximumRequestedUrlReached(nbTreatedUrls)) {
					THREAD_LOGGER.debug("Shutdown scheduler");
					scheduler.awaitTermination(spiderConf.getSchedulerTerminationWaitingTime(), TimeUnit.MILLISECONDS);
					scheduler.shutdownNow();
					return;
				}
			} catch (InterruptedException e) {
				THREAD_LOGGER.error("", e);
			}
		}
	}

	private boolean isMaximumRequestedUrlReached(int nbRequest) {
		return spiderConf.getMaximumTotalRequestedUrls() > 0 && nbRequest >= spiderConf
				.getMaximumTotalRequestedUrls();
	}

	private class SpiderThread extends Thread {
		private String url;
		private UrlLister urlLister;
		private int urlIndex;

		public SpiderThread(String url, UrlLister urlLister, int urlIndex) {
			this.url = url;
			this.urlLister = urlLister;
			this.urlIndex = urlIndex;
		}

		@Override
		public void run() {
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

						if (result.getError()) {
							LOGGER.error("Error " + result.getUrl() + " : " + result.getMessages());
						} else {
							boolean callback = false;
							for (String patternCallback : mapSpiderCallback.keySet()) {
								if (url.matches(patternCallback)) {
									mapSpiderCallback.get(patternCallback).callback(result,
											urlLister);
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

			try {
				Thread.sleep(spiderConf.getTimeBetweenSearchs());
			} catch (InterruptedException e) {
			}

			THREAD_LOGGER.debug("End SpiderThread " + getName());
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
		scheduler = Executors.newScheduledThreadPool(spiderConf.getNbThreads());
	}

}
