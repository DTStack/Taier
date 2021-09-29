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


import { message, notification } from 'antd'
import { Cookie, LocalDB } from '@dtinsight/dt-utils'

const maxHeightStyle: any = {
    maxHeight: '500px',
    overflowY: 'auto'
}

export function authBeforeFormate (response: any) {
    switch (response.status) {
        case 402:
        case 200:
        case 412:
            // return versionMonitor(response, ['/api/console', '/node'], 'DT_CONSOLE');
            return response;
        case 302:
            // return versionMonitor(response, ['/api/console', '/node'], 'DT_CONSOLE');
            message.info('登录超时, 请重新登录！')
            return Promise.reject(response);
        case 500:
            message.error('服务器出现了点问题')
            return Promise.reject(response);
        default:
            // if (process.env.NODE_ENV !== 'production') {
            //     console.error('Request error: ', response.code, response.message)
            // }
            // return versionMonitor(response, ['/api/console', '/node'], 'DT_CONSOLE');
            if (response.message) {
                message.error(response.message, 3) // 异常消息默认显示5s
            }
            return response
    }
}

export function authAfterFormated (response: any) {
    switch (response.code) {
        case 1:
            return response;
        case 0: // 无权限，需要登录

            // UserApi.logout()
            return response;
        case 3: // 功能无权限
            notification['error']({
                message: '权限通知',
                description: response.message
            });
            return Promise.reject(response);
        case 16: // 需要重新进入Web首页选择项目，并进入
            // hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                setTimeout(() => {
                    notification['error']({
                        message: '异常',
                        description: response.message,
                        style: { ...maxHeightStyle, wordBreak: 'break-all' }
                    });
                }, 0)
            }
            return response
    }
}

export function isSelectedProject () {
    const pid = Cookie.getCookie('project_id')
    if (!pid || pid === 'undefined') {
        Cookie.deleteCookie('project_id')
        // browserHistory.push('/')
    }
}

export function isLogin () {
    return LocalDB.get('session')
}
