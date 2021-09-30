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

export let checks = [];
export const saveCheckStauts = (checkdList) => {
	checks = checkdList;
};

//清除数据
export function remove() {
	sessionStorage.removeItem('current');
	sessionStorage.removeItem('sqlType');
	sessionStorage.removeItem('version');
	saveCheckStauts([]);
}

export function getSaveStatus() {
	let sqlType: any = {
		dataType: '',
	};
	try {
		sqlType = JSON.parse(sessionStorage.getItem('sqlType'));
	} catch (error) {}
	let saveStatus = {
		menuSelected: sessionStorage.getItem('current'),
		sqlType,
		version: sessionStorage.getItem('version') || '',
	};

	return saveStatus;
}
