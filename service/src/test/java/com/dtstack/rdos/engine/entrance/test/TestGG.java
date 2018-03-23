package com.dtstack.rdos.engine.entrance.test;

import java.util.Map;

import com.google.common.collect.Maps;


/**
 * Created by sishu.yss on 2017/5/25.
 */
public class TestGG {
	
	public static void main(String[] args){
		Map<String,Object> kk = Maps.newLinkedHashMap();
		kk.put("ysqys", 123);
		kk.put("ffg", 1234);
		while(kk.keySet().iterator().hasNext()){
			kk.remove(kk.keySet().iterator().next());
		}
		System.out.println(kk.size());
	}
}
