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
import Base64 from 'base-64';

import api from '@/api';
import { IDataSourceProps } from '@/interface';

export interface IDataSourceState {
    dataSource: IDataSourceProps[];
}

interface IDataSourceService {
    getDataSource: () => IDataSourceProps[];
    reloadDataSource: () => void;
}

export default class DataSourceService extends Component<IDataSourceState> implements IDataSourceService {
    protected state: IDataSourceState = {
        dataSource: [],
    };

    constructor() {
        super();
        this.queryDataSource();
    }

    private queryDataSource = () => {
        api.getAllDataSource({}).then((res) => {
            if (res.code === 1) {
                const nextData: IDataSourceProps[] = ((res.data as IDataSourceProps[]) || []).map((ele) => {
                    const canConvertLinkJson =
                        ele.linkJson && !ele.linkJson.includes('{') && !ele.linkJson.includes('}');

                    return {
                        ...ele,
                        linkJson: canConvertLinkJson ? Base64.decode(ele.linkJson!) : ele.linkJson,
                    };
                });

                this.setState({
                    dataSource: nextData,
                });
            }
        });
    };

    getDataSource = () => {
        return this.state.dataSource || [];
    };

    reloadDataSource = () => {
        this.queryDataSource();
    };
}
