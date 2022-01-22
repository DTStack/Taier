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

export default interface HttpInterface {
    /**
     * HTTP Get method
     * @param url request URL
     * @param params  request Parameter
     */
    get<R, P = {}>(url: string, params?: P): Promise<R>;
    /**
     * HTTP Post method
     * @param url request URL
     * @param body request body object
     */
    post<R, P = {}>(url: string, body?: P): Promise<R>;
    /**
     * Post an object as a formData object
     * @param url request URL
     * @param params the params object that wait to convert to formData
     */
    postAsFormData<R, P = {}>(url: string, params?: P): Promise<R>;
    /**
     * Post a form element
     * @param url request URL
     * @param form HTML Form element
     */
    postForm<R>(url: string, form: HTMLElement): Promise<R>;
    /**
     * Http request
     * @param url request URL
     * @param options request options
     */
    request<R>(url: string, options?: RequestInit): Promise<R>;
}
