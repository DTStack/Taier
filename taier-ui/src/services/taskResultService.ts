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

import { Component } from '@dtinsight/molecule/esm/react';
import moment from 'moment';

export interface ITaskResultService {
    /**
     * 增量添加日志信息
     *
     * 自动添加换行标识符
     */
    appendLogs: (key: string, log: string) => void;
    /**
     * 清除任务执行的日志信息
     */
    clearLogs: (key: string) => void;
    /**
     * 任务执行结果
     */
    setResult: (key: `${string}-${string}`, result: any) => void;
    /**
     * 清除执行结果
     */
    clearResult: (key: string) => void;
}

/**
 * 输出固定格式的日志前缀
 * @example `[14:00:01] <error> xxxx`
 */
export function createLog(log: string, type = 'info') {
    return `[${moment().format('HH:mm:ss')}] <${type}> ${log}`;
}

/**
 * 生成固定格式的 title
 * @example `=====title=====`
 * @example `===============`
 */
export function createTitle(title = '') {
    const baseLength = 15;
    const offsetLength = Math.floor((1.5 * title.length) / 2);
    const arr = new Array(Math.max(baseLength - offsetLength, 5));
    const wraptext = arr.join('=');
    return `${wraptext}${title}${wraptext}`;
}

export function createLinkMark(attrs: Record<string, string | null>) {
    return `${window.location.origin}${attrs.href}`;
}
export interface ITaskResultStates {
    /**
     * 存储不同任务执行的日志结果
     */
    logs: Record<string, string>;
    /**
     * 任务执行结果
     * @key 前者为 tabId，后者为基于 sqlText 生成的 md5 码
     */
    results: Record<`${string}-${string}`, string[][]>;
}

class TaskResultService extends Component<ITaskResultStates> implements ITaskResultService {
    protected state: ITaskResultStates;

    constructor() {
        super();
        this.state = {
            logs: {},
            results: {},
        };
    }

    public setResult(key: `${string}-${string}`, res: any) {
        const nextResults = this.state.results;
        nextResults[key] = res;
        this.setState({ results: { ...nextResults } });
    }

    public clearResult(key: string) {
        const nextResults = this.state.results;
        Reflect.deleteProperty(nextResults, key);
        this.setState({ results: { ...nextResults } });
    }

    public appendLogs(key: string, log: string) {
        const nextLogs = this.state.logs;
        nextLogs[key] = nextLogs[key] || '';
        nextLogs[key] += `${log}\n`;
        this.setState({
            logs: { ...nextLogs },
        });
    }

    public clearLogs(key: string) {
        const nextLogs = this.state.logs;
        nextLogs[key] = '';
        this.setState({
            logs: { ...nextLogs },
        });
    }
}

/**
 * 处理任务执行结果的 service
 */
export default new TaskResultService();
