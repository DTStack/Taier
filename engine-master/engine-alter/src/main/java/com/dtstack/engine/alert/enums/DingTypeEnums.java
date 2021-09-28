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

package com.dtstack.engine.alert.enums;

public enum DingTypeEnums {
	
	TEXT("text"),LINK("link"),MARKDOWN("markdown"),ACTION_CARD("actionCard"),FEED_CARD("feedCard");

	private String msg;

	DingTypeEnums(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public static DingTypeEnums getDingTypeEnum(String type){
		if("text".equals(type)){
			return TEXT;
		} else if("markdown".equals(type)){
			return MARKDOWN;
		}
		return MARKDOWN;
	}

}
