package com.jspider.model.urls;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UrlLister extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213976523111703513L;
	private Set<String> alreadyReadUrls;

	private int urlIndex = 0;

	public UrlLister(int urlIndex) {
		this.setUrlIndex(urlIndex);
		alreadyReadUrls = new HashSet<String>();
	}

	@Override
	public boolean add(String e) {
		if (!alreadyReadUrls.contains(e)) {
			alreadyReadUrls.add(e);
			return super.add(e);
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		boolean ret = true;
		for (String s : c) {
			ret &= add(s);
		}
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map.Entry<String, Integer> getNext() {
		String url = this.iterator().next();
		this.remove(url);
		return new AbstractMap.SimpleEntry(url, urlIndex++);
	}

	public int getUrlIndex() {
		return urlIndex;
	}

	public void setUrlIndex(int urlIndex) {
		this.urlIndex = urlIndex;
	}
}
