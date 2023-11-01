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
import { SearchOutlined } from '@ant-design/icons';
import { Form, Input,Select } from 'antd';

import API from '@/api';
import './search.scss';

interface IProps {
    onSearch: (value: IFormFieldProps) => void;
}

const { Option } = Select;

interface ITypeProps {
    dataType: string;
}

interface IFormFieldProps {
    search: string;
    dataTypeList: string[];
}

export default function Search({ onSearch }: IProps) {
    const [form] = Form.useForm<IFormFieldProps>();
    const [typeList, setTypeList] = useState<ITypeProps[]>([]);

    const handleSearch = () => {
        const { search = '', dataTypeList } = form.getFieldsValue();
        onSearch({
            search: search.trim(),
            dataTypeList,
        });
    };

    const getTypeList = async () => {
        const { data, success } = await API.typeList({});

        if (success) {
            setTypeList(data || []);
        }
    };

    useEffect(() => {
        getTypeList();
    }, []);

    return (
        <div className="top-search">
            <Form<IFormFieldProps> form={form} wrapperCol={{ span: 24 }} autoComplete="off">
                <Form.Item name="search">
                    <Input
                        placeholder="数据源名称/描述"
                        onPressEnter={() => handleSearch()}
                        suffix={<SearchOutlined onClick={() => handleSearch()} style={{ cursor: 'pointer' }} />}
                    />
                </Form.Item>
                <Form.Item name="dataTypeList">
                    <Select<string[]>
                        mode="multiple"
                        placeholder="请选择类型"
                        allowClear
                        showSearch
                        maxTagCount={1}
                        showArrow
                        optionFilterProp="children"
                        onChange={() => handleSearch()}
                    >
                        {typeList.map((item) => {
                            return (
                                <Option value={item.dataType} key={item.dataType}>
                                    {item.dataType}
                                </Option>
                            );
                        })}
                    </Select>
                </Form.Item>
            </Form>
        </div>
    );
}
