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

import { queryParse } from '@/utils';
import Swagger from './dataSources-swagger';
import http from './http';

type APIProps = Record<
	keyof typeof Swagger,
	(params: any, config?: any) => Promise<any>
>;

function mapUrlObjToFuncObj(urlObj: typeof Swagger) {
	const keys = Object.keys(urlObj) as (keyof typeof Swagger)[];
	return keys.reduce((pre, cur) => {
		const item = urlObj[cur];
		pre[cur] = async function (params, config: any = {}) {
			const queryParams: any = queryParse(window.location.hash);
			// 所有的请求接口统一带上header
			if (queryParams.datasource_id) {
				config.headers = new Headers({
					dt_datasource_id: queryParams.datasource_id,
				});
			}
			return await http[item.method as 'get' | 'post' | 'postForm'](
				item.url,
				params,
				config,
			);
		};
		return pre;
	}, {} as APIProps);
}
// function mapUrlObjToStrObj(urlObj) {
//   const Url = {};
//   keys(urlObj).forEach((key) => {
//     const item = urlObj[key];
//     Url[key] = item.url;
//   });
//   return Url;
// }

export const API = mapUrlObjToFuncObj(Object.assign({}, Swagger));
// export const URL = mapUrlObjToStrObj(Object.assign({}, Swagger, Restful));
