package com.jspider.spider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncLog.LoggerThread;
import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.ResultSearcher;
import com.jspider.metier.resultSearcher.model.ResultsModel;
import com.jspider.model.Search;
import com.jspider.spider.configuration.SpiderConfiguration;

public class Spider {
	private final static Logger LOGGER = LoggerFactory.getLogger(Spider.class);

	/**
	 * Map url regex -> ResultSearcher
	 */
	private Map<String, ResultSearcher> mapResultSearcher;
	private Map<String, SpiderCallBack> mapSpiderCallback;

	private SpiderConfiguration spiderConf;

	private ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(40);

	private List<LoggerThread> loggers;

	public void spider(Collection<String> urls) throws ApplicationException {
		for (LoggerThread t : loggers) {
			scheduler.scheduleAtFixedRate(t, t.getMs(), t.getMs(),
					TimeUnit.MILLISECONDS);
		}
		while (true) {
			while (!urls.isEmpty()) {
				String url = urls.iterator().next();
				urls.remove(url);
				Runnable worker = new SpiderThread(url, urls);
				scheduler.execute(worker);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error("", e);
			}
		}
	}

	private class SpiderThread extends Thread {
		private String url;
		private Collection<String> urls;

		public SpiderThread(String url, Collection<String> urls) {
			this.url = url;
			this.urls = urls;
		}

		@Override
		public void run() {
			boolean match = false;
			for (String pattern : mapResultSearcher.keySet()) {
				if (url.matches(pattern)) {
					match = true;
					Search s = new Search(url);
					ResultsModel result;
					try {
						result = mapResultSearcher.get(pattern).search(s);

						if (result.getError()) {
							LOGGER.error("Error " + result.getUrl() + " : "
									+ result.getMessages());
						} else {
							if (mapSpiderCallback.containsKey(pattern)) {
								mapSpiderCallback.get(pattern).callback(result,
										urls);
								break;
							} else {
								LOGGER.warn("no matching callback for url "
										+ url);
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

}
