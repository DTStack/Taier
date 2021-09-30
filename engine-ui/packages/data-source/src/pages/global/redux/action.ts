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

import { globalType } from './actionType';

const userData = (data) => ({
	type: globalType.GET_USER_DATA,
	payload: data,
});
export const getUserData =
	(params) =>
	async (dispatch, getState, { API }) => {
		try {
			const { meta, data } = await API.getUserData(params);
			if (meta && meta.success) {
				dispatch(userData(data));
			}
		} catch (ex) {
			console.warn(ex);
		}
	};

const navData = (data) => ({
	type: globalType.GET_NAV_DATA,
	payload: data,
});
export const getNavData =
	(params) =>
	async (dispatch, getState, { API }) => {
		try {
			const { meta, data } = await API.getUserData(params);
			if (meta && meta.success) {
				dispatch(navData(data));
			}
		} catch (ex) {
			console.warn(ex);
		}
	};
