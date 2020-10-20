package com.dtstack.engine.common.util;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public class RenderUtil {

	public static String renderTemplate(String template, Map<String, String> param) {
		StrSubstitutor sub = new StrSubstitutor(param);
		return sub.replace(template);
	}

}
