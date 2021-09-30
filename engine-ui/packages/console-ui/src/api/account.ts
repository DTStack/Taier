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

import http from './http';
import req from '../consts/reqUrls';
import { numOrStr } from 'typing';

export interface IAccount {
	name: string;
	username: string;
	password: string;
	bindUserId: string;
	bindTenantId: string;
	email: string;
	engineType: string | number;
}

export default {
	getUnbindAccounts(params: { dtuicTenantId: numOrStr; engineType: numOrStr }) {
		return http.post(req.ACCOUNT_UNBIND_LIST, params);
	},

	bindAccount(params: IAccount) {
		return http.postWithDefaultHeader(req.ACCOUNT_BIND, params);
	},

	ldapBindAccount(params: { accountList: any[] }) {
		return http.postWithDefaultHeader(req.LDAP_ACCOUNT_BIND, params);
	},

	updateBindAccount(params: IAccount) {
		return http.postWithDefaultHeader(req.UPDATE_ACCOUNT_BIND, params);
	},

	getBindAccounts(params: { dtuicTenantId: numOrStr; username: string; engineType: numOrStr }) {
		return http.post(req.ACCOUNT_BIND_LIST, params);
	},

	unbindAccount(params: { id: numOrStr; name: string; password: string }) {
		return http.postWithDefaultHeader(req.ACCOUNT_UNBIND, params);
	},

	// 获取UIC租户
	getFullTenants(value?: any) {
		const getTenantsUrl = `${req.GET_FULL_TENANT + (value || '')}`;
		return http.get(getTenantsUrl);
	},

	getTenantsList() {
		return http.post(req.GET_TENANTS_LIST);
	},
};
