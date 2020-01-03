package com.dtstack.engine.service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sishu.yss on 2017/5/25.
 */
public class TestGG {
	
	public static void main(String[] args) throws Exception{

		List<Object> list = new ArrayList<>();
		int i = 0;
		while (true) {
			System.out.println("omObject");

			list.add(new OMObject());
			System.out.println("loop: " + i++);
			if (i>10){
				Thread.sleep(3000);
			}
		}
	}

	public static class OMObject {

		private byte[] OM_OBJECT;

		public OMObject() {
			this.OM_OBJECT = new byte[1024 * 1024 * 2];
		}
	}
}
