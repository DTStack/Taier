package com.dtstack.engine.entrance;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class TestCase {
	
	public static void main(String[] args){
		String ss = "828aeb8d22ed96fd163def0045ef35d5";
		System.out.println(ss.getBytes().length);
		System.out.println(1024*1024/40);
		Map<String,String> jj = Maps.newHashMap();
		jj.put("yysq", "yysq");
		Iterator hh = jj.keySet().iterator();
		while(hh.hasNext()){
			jj.remove(hh.next());
		}
		System.out.println(jj);
	}

}
