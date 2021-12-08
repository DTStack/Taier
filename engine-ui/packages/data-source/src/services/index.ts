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

import { Fetch } from 'ko-request';
import Restful from './restful';
import Swagger from './swagger';
import queryParse from '@/utils/queryParser';
const http = new Fetch();
const { keys } = Object;

function mapUrlObjToFuncObj(urlObj) {
  const API = {};
  keys(urlObj).forEach((key) => {
    const item = urlObj[key];
    API[key] = async function (params, config: any = {}) {
      const queryParams: any = queryParse(window.location.hash);
      // 所有的请求接口统一带上header
      if (queryParams.datasource_id) {
        config.headers = new Headers({
          dt_datasource_id: queryParams.datasource_id,
        });
      }
      return await http[item.method](item.url, params, config);
    };
  });
  return API;
}
function mapUrlObjToStrObj(urlObj) {
  const Url = {};
  keys(urlObj).forEach((key) => {
    const item = urlObj[key];
    Url[key] = item.url;
  });
  return Url;
}

export const API: any = mapUrlObjToFuncObj(Object.assign({}, Swagger, Restful));
export const URL: any = mapUrlObjToStrObj(Object.assign({}, Swagger, Restful));
