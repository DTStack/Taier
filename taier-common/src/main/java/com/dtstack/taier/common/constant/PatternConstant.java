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

package com.dtstack.taier.common.constant;


public interface PatternConstant {

	String FUNCTION_PATTERN = "[a-z0-9_]+";

	/**
	 * 正则: 租户名称正则表达式,字母、数字、下划线组成，且长度不超过64个字符
	 * Regular: Tenant name regular expression, consisting of letters, numbers, and underscores, and the length does not exceed 64 characters
	 */
	String TENANT_NAME_REGEX = "^[a-zA-Z0-9_]{1,64}$";

}
