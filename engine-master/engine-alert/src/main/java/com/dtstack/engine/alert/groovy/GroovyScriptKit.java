package com.dtstack.engine.alert.groovy;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

//import groovy.lang.GroovyShell;
//import groovy.lang.Script;

/**
 * <p>
 * groovy脚本引擎
 * </p>
 * 
 * @author 青涯
 */
public class GroovyScriptKit {

	private static String code = "$code";

	private static String funcCode = "$funcCode";

	private static String call_template = String.format("%s \n def call(def param){return %s}", funcCode, code);

//	private static Cache<String, Script> scriptCache = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).maximumSize(10000).build();

	private static String groovyFlag = "dt_g_";
	
	public static Object executeGroovy(String groovyScript, String groovyFunc, Object param) {
//		String ct = call_template.replace(code, groovyScript);
//		// 加入函数代码
//		if (groovyFunc != null && !groovyFunc.isEmpty()) {
//			ct = ct.replace(funcCode, groovyFunc);
//		}
//		String md5 = Md5Crypt.md5Crypt(groovyScript.getBytes());
//		Script script = scriptCache.getIfPresent(md5);
//		if (script == null) {
//			GroovyShell shell = new GroovyShell();
//			script = shell.parse(ct);
//			scriptCache.put(md5, script);
//		}
//		return script.invokeMethod("call", param);//传递参数进去，避免混编语言类型不一致问题
		return null;
	}

	public static Object parseGroovy(String content, String groovyFunc, Map<String, Object> dynamicParam) {
		while (content.contains(groovyFlag)) {
			FunctionEntity functionEntity = extractFunc(content, dynamicParam);
			String script = String.format("%s(param)", functionEntity.getFunctionName());
			String replacement = (String) executeGroovy(script, groovyFunc, functionEntity.getFunctionParam());
			content = content.replace(functionEntity.getFunctionBody(), replacement);
		}
		return content;
	}

	public static boolean isGroovySyntax(String content) {
		if (content != null && content.contains(groovyFlag)) {
			return true;
		}
		return false;
	}

	private static FunctionEntity extractFunc(String content, Map<String, Object> dynamicParam) {
		int startIndex = content.lastIndexOf(groovyFlag);
		int endIndex = content.length();
		int leftBracketIndex = content.indexOf("(", startIndex);
		int leftBracketCnt = 0;
		for (int i = leftBracketIndex; i < content.length(); i++) {
			if (content.charAt(i) == '(') {
				leftBracketCnt++;
			} else if (content.charAt(i) == ')') {
				leftBracketCnt--;
				if (leftBracketCnt == 0) {
					endIndex = i + 1;
					break;
				}
			}
		}
		String functionBody = content.substring(startIndex, endIndex);
		String functionName = content.substring(startIndex, leftBracketIndex);
		Object functionParam = getFunctionParam(content.substring(leftBracketIndex + 1, endIndex - 1), dynamicParam);
		return new FunctionEntity(functionName, functionParam, functionBody);
	}
	
	private static Object getFunctionParam(String content, Map<String, Object> dynamicParam){
		Map<String, String> param = Maps.newHashMap();
		dynamicParam.entrySet().forEach(x->{
			if(x.getValue() != null){
				param.put(x.getKey(), JSONObject.toJSONString(x.getValue()));
			}
		});
		StrSubstitutor sub = new StrSubstitutor(param);
		return sub.replace(content);
	}
}

class FunctionEntity {

	String functionName;

	Object functionParam;

	String functionBody;

	public FunctionEntity(String functionName, Object functionParam, String functionBody) {
		this.functionName = functionName;
		this.functionParam = functionParam;
		this.functionBody = functionBody;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Object getFunctionParam() {
		return functionParam;
	}

	public void setFunctionParam(Object functionParam) {
		this.functionParam = functionParam;
	}

	public String getFunctionBody() {
		return functionBody;
	}

	public void setFunctionBody(String functionBody) {
		this.functionBody = functionBody;
	}

}
