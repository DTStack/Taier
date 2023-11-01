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

import api from '@/api';
import { PARAMS_ENUM } from '@/constant';

export interface IParamsProps {
    id?: number;
    paramCommand: string;
    paramName: string;
    type: PARAMS_ENUM;
}

interface ITaskParamsService {
    /**
     * 根据 sql 匹配系统参数和自定义参数
     */
    matchTaskParams: (sql: string) => Partial<IParamsProps>[];
}

interface ITaskParamsStates {
    systemParams: IParamsProps[];
}

export default class TaskParamsService extends Component<ITaskParamsStates> implements ITaskParamsService {
    protected state: ITaskParamsStates;
    constructor() {
        super();
        this.state = {
            systemParams: [],
        };
        this.getSystemParams();
    }

    private getSystemParams = () => {
        api.getCustomParams().then((res) => {
            if (res.code === 1) {
                this.setState({
                    systemParams: res.data ?? [],
                });
            }
        });
    };

    public matchTaskParams = (sqlText: string) => {
        const regx = /\$\{([.\w]+)\}/g;
        const data: Partial<IParamsProps>[] = [];
        let res = null;
        while ((res = regx.exec(sqlText)) !== null) {
            const name = res[1];
            const param: Partial<IParamsProps> = {
                paramName: name,
                paramCommand: '',
            };
            const sysParam = this.state.systemParams.find((item) => item.paramName === name);
            if (sysParam) {
                param.type = PARAMS_ENUM.SYSTEM;
                param.paramCommand = sysParam.paramCommand;
            } else {
                param.type = PARAMS_ENUM.CUSTOM;
            }
            // 去重
            const exist = data.find((item) => name === item.paramName);
            if (!exist) {
                data.push(param);
            }
        }
        return data;
    };
}
