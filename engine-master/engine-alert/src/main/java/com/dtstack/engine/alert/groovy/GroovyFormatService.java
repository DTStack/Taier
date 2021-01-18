package com.dtstack.engine.alert.groovy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 执行groovy脚本，格式化发往第三方通道的参数
 * 
 * @author dapeng
 * @Date 2018年5月10日
 * @Motto 孤云出岫,朗月悬空
 */
@Service
public class GroovyFormatService {

//	@Autowired
//	private GroovyScriptService groovyScriptService;

	public Map<String, String> formatMapStr(Map<String, String> param, Map<String, Object> dynamicParam) {
		Map<String, String> result = Maps.newHashMap();
		Iterator<Entry<String, String>> iterator = param.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			result.put(formatStr(entry.getKey(), dynamicParam), formatStr(entry.getValue(), dynamicParam));
		}
		return result;
	}

	public Map<String, Object> formatMap(Map<String, Object> param, Map<String, Object> dynamicParam) {
		Map<String, Object> result = Maps.newHashMap();
		Iterator<Entry<String, Object>> iterator = param.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			result.put(formatStr(entry.getKey(), dynamicParam), formatObj(entry.getValue(), dynamicParam));
		}
		return result;
	}

	public String formatStr(String param, Map<String, Object> dynamicParam) {
		String result = param;
		if (GroovyScriptKit.isGroovySyntax(param)) {
//			String groovyFunc = groovyScriptService.getGroovyFunc();
//			result = (String) GroovyScriptKit.parseGroovy(param, groovyFunc, dynamicParam);
		}
		return placeHolder(result, dynamicParam);
	}

	public Collection<Object> formatCollection(Collection<Object> param, Map<String, Object> dynamicParam) {
		Collection<Object> result = Lists.newArrayList();
		for (Object meta : param) {
			result.add(formatObj(meta, dynamicParam));
		}
		return result;
	}

	public Object formatObj(Object param, Map<String, Object> dynamicParam) {
		if (param instanceof Map) {
			return formatMap((Map) param, dynamicParam);
		}
		if (param instanceof List) {
			return formatCollection((List) param, dynamicParam);
		}
		if (param instanceof String) {
			String msg = formatStr((String) param, dynamicParam);
			if(msg.startsWith("[") && msg.endsWith("]")){
				return StringUtils.split(StringUtils.substring(msg, 1, msg.length() - 1), ",");
			}
			return msg;
		}
		return param;
	}

	/**
	 * 占位符
	 * @param content
	 * @param dynamicParam
	 * @return
	 */
	private String placeHolder(String content, Map<String, Object> dynamicParam) {
		StrSubstitutor sub = new StrSubstitutor(dynamicParam);
		return sub.replace(content);
	}
}
