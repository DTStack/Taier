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

import 'whatwg-fetch'

import { ProgressBar } from 'dt-react-component'

import { authAfterFormated, authBeforeFormate } from '../interceptor'
class Http {
    get (url: any, params?: any) { // GET请求
        const newUrl = params ? this.build(url, params) : url
        return this.request(newUrl, {
            method: 'GET'
        })
    }

    post (url: any, body?: any) { // POST请求
        let options: any = { method: 'POST' }
        if (body) options.body = JSON.stringify(body)
        return this.request(url, options)
    }

    // account 相关接口需要设置默认请求头
    postWithDefaultHeader (url: any, body: any) { // POST请求
        let options: any = { method: 'POST' }
        options.headers = this.defaultHeader()
        if (body) options.body = JSON.stringify(body)
        return this.request(url, options)
    }

    postAsFormData (url: any, params: any) {
        let options: any = { method: 'POST' }
        if (params) options.body = this.buildFormData(params)
        return this.request(url, options)
    }

    postForm (url: any, form: any) {
        let options: any = { method: 'POST' }
        if (form) options.body = new FormData(form)
        return this.request(url, options)
    }

    request (url: any, options: any) {
        ProgressBar.show()
        options.credentials = 'same-origin'
        return fetch(url, options)
            .then(authBeforeFormate)
            .then((response: any) => {
                setTimeout(() => {
                    ProgressBar.hide()
                }, 300)
                return response.json()
            })
            .then(authAfterFormated)
            .catch((err: any) => {
                console.error(url + ':' + err)
                /* eslint-disable-next-line */
                return Promise.reject(err);
            })
    }

    defaultHeader () { // 默认头
        const header: any = {
            'Accept': '*/*',
            'Content-Type': 'application/json'
        };
        return header
    }

    build (url: any, params: any) { // URL构建方法
        const ps: any = []
        if (params) {
            for (let p in params) {
                if (p) {
                    ps.push(p + '=' + encodeURIComponent(params[p]));
                }
            }
        }
        return url + '?' + ps.join('&')
    }

    buildFormData (params: any) {
        if (params) {
            const data = new FormData()
            for (let p in params) {
                if (p) {
                    data.append(p, params[p])
                }
            }
            return data;
        }
    }
}
export default new Http()
