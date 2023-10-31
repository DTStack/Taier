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

import { useEffect, useState } from 'react';
import { message,Select } from 'antd';

import API from '@/api';
import type { IDataSourceType } from './add';
import './version.scss';

const { Option } = Select;

interface IVersionListProps {
    dataType: string;
    dataVersion: string;
    sorted: number;
}

interface IVersionProps {
    dataSource: IDataSourceType;
    onSelectVersion?: (version: string) => void;
}

const DISABLED_SOURCE = ['Kafka@0.9'];

function Version({ dataSource, onSelectVersion }: IVersionProps) {
    const [version, setVersion] = useState<IVersionListProps[]>([]);
    const [selectedVersion, setSelectedVersion] = useState<string>('');

    // 根据数据源类型获取版本列表
    const queryDsVersionByType = async () => {
        const { dataType } = dataSource;
        const { data, success } = await API.queryDsVersionByType({
            dataType,
        });
        if (success) {
            setVersion(data || []);
            setSelectedVersion(data[0].dataVersion);
        } else {
            message.error('根据数据源类型获取版本列表失败！');
        }
    };

    // 存储数据库版本
    const handleSelectVersion = (value: string) => {
        setSelectedVersion(value);
    };

    useEffect(() => {
        queryDsVersionByType();
    }, []);

    useEffect(() => {
        if (selectedVersion) {
            onSelectVersion?.(selectedVersion);
        }
    }, [selectedVersion]);

    return (
        <div className="produce-auth">
            <div className="text-show">
                {version.length > 0 && (
                    <div className="version-sel">
                        <span className="version">
                            <b
                                style={{
                                    color: 'red',
                                    verticalAlign: 'center',
                                }}
                            >
                                *
                            </b>
                            版本：
                        </span>
                        <Select
                            showSearch
                            style={{ width: '80%' }}
                            placeholder="请选择对应版本"
                            optionFilterProp="children"
                            onChange={handleSelectVersion}
                            value={selectedVersion}
                        >
                            {version.map((item) => {
                                return (
                                    <Option
                                        value={item.dataVersion}
                                        disabled={DISABLED_SOURCE.includes(
                                            `${dataSource.dataType}@${item.dataVersion}`
                                        )}
                                        key={item.dataVersion}
                                    >
                                        {item.dataVersion}
                                    </Option>
                                );
                            })}
                        </Select>
                    </div>
                )}
            </div>
        </div>
    );
}
export default Version;
