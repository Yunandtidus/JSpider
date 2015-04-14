package com.asyncLog.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncLog.service.AsyncLogService;

public class AsyncLogServiceImpl implements AsyncLogService {

	private static final Logger logger = LoggerFactory
			.getLogger(AsyncLogServiceImpl.class);

	private String filePath;

	private Map<String, AtomicInteger> counters;

	public AsyncLogServiceImpl() {
		counters = new ConcurrentHashMap<String, AtomicInteger>();
	}

	@Override
	public void incrCounter(String name) {
		if (counters.containsKey(name)) {
			counters.get(name).incrementAndGet();
		} else {
			counters.put(name, new AtomicInteger(1));
		}

	}

	@Override
	public void logCounter(String name, String filePrefix) {
		try {
			File f = new File(filePath + filePrefix + "_" + name + ".log");
			if (!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f, true);
			AtomicInteger ai = counters.get(name);
			StringBuilder sb = new StringBuilder();
			sb.append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
					.format(Calendar.getInstance().getTime()));
			sb.append(" ");
			sb.append(ai == null ? 0 : Integer.toString(ai.getAndSet(0)));
			sb.append("\n");
			fos.write(sb.toString().getBytes());
			logger.info(name + " " + sb.toString());
			fos.close();
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
