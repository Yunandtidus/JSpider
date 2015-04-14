package com.jspider.daemon;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jspider.exception.ApplicationException;
import com.jspider.metier.resultSearcher.ResultSearcher;
import com.jspider.model.Search;

public class Main {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring/config/BeanLocations.xml" });
		try {
			Search search = new Search();
			search.getCriteria().put("keywords", "206");
			search.getCriteria().put("minPrice", "2000");
			search.getCriteria().put("maxPrice", "10000");
			search.getCriteria().put("energy", "diesel");
			context.getBean("leboncoinResultSearcher", ResultSearcher.class)
					.search(search);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
}
