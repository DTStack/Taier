/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.zookeeper.data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BrokerHeartNode {
	
	private  Long seq;
	
	private  Boolean alive;

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Boolean getAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	public static BrokerHeartNode initBrokerHeartNode(){
		BrokerHeartNode brokerHeartNode = new BrokerHeartNode();
		brokerHeartNode.setAlive(true);
		brokerHeartNode.setSeq(0L);
		return brokerHeartNode;
	}
	
	public static BrokerHeartNode initNullBrokerHeartNode(){
		BrokerHeartNode brokerHeartNode = new BrokerHeartNode();
		brokerHeartNode.setSeq(0L);
		brokerHeartNode.setAlive(false);
		return brokerHeartNode;
	}
	
	public static void copy(BrokerHeartNode source,BrokerHeartNode target,boolean isCover){
		if(isCover){
			target.setSeq(source.getSeq());
		}else{
	    	if(source.getSeq()!=null){
	    		target.setSeq(source.getSeq()+target.getSeq());
	    	}
		}
    	if(source.getAlive()!=null){
    		target.setAlive(source.getAlive());
    	}
	}
}
