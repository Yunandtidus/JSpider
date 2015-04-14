package com.jspider.utils;

import java.util.List;

public class ListUtils {
	public static boolean nullOrEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}
}
