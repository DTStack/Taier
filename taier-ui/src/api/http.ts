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

import { message } from 'antd';
import 'whatwg-fetch';

import notification from '@/components/notification';
import ProgressBar from '@/components/progressBar';
import type { IResponseBodyProps } from '@/interface';

class Http {
    /**
     * 是否输出错误信息
     */
    public verbose = true;
    get<T = any>(url: any, params: any, config: Record<string, any> = {}) {
        // GET请求
        const newUrl = params ? this.build(url, params) : url;
        return this.request<T>(newUrl, {
            method: 'GET',
            ...config,
        });
    }

    post<T = any>(url: any, body?: any, config: Record<string, any> = {}) {
        // POST请求
        const options: any = { method: 'POST', ...config };
        if (body)
            options.body = JSON.stringify({
                ...body,
            });
        return this.request<T>(url, options);
    }

    postAsFormData(url: any, params: any) {
        const options: any = { method: 'POST' };
        if (params) options.body = this.buildFormData(params);
        return this.request(url, options);
    }

    postForm(url: any, params: any) {
        const options: any = { method: 'POST' };
        if (params) options.body = this.buildFormData(params);
        return this.request(url, options);
    }

    // account 相关接口需要设置默认请求头
    postWithDefaultHeader(url: any, body: any) {
        // POST请求
        const options: any = { method: 'POST' };
        options.headers = this.defaultHeader();
        if (body) options.body = JSON.stringify(body);
        return this.request(url, options);
    }

    request<T = any>(url: string, options: RequestInit) {
        ProgressBar.show();
        return fetch(url, { ...options, credentials: 'same-origin' })
            .then((response) => {
                setTimeout(() => {
                    ProgressBar.hide();
                }, 300);
                return response.json();
            })
            .then((res: IResponseBodyProps<T>) => {
                if (res.code !== 1 && res.message === '未登录') {
                    notification.error({
                        key: 'NotLogin',
                        message: `未登录，请登陆后进行操作`,
                    });
                }
                return res;
            })
            .then((res) => {
                if (res.code !== 1) {
                    if (this.verbose) {
                        // 相同的错误文案只提示一次
                        message.error({ content: res.message, key: res.message });
                    }
                }
                return res;
            })
            .catch((err: Error) => {
                setTimeout(() => {
                    ProgressBar.hide();
                }, 300);
                if (err.name === 'AbortError') {
                    err.stack = '';
                }
                throw err;
            });
    }

    defaultHeader() {
        // 默认头
        const header: any = {
            Accept: '*/*',
            'Content-Type': 'application/json',
        };
        return header;
    }

    build(url: any, params: any) {
        // URL构建方法
        const ps: any = [];
        if (params) {
            for (const p in params) {
                if (p) {
                    ps.push(p + '=' + encodeURIComponent(params[p]));
                }
            }
        }
        return url + '?' + ps.join('&');
    }

    buildFormData(params: any) {
        if (params) {
            const data = new FormData();
            for (const p in params) {
                if (p) {
                    data.append(p, params[p] ?? '');
                }
            }
            return data;
        }
    }
}
export default new Http();
