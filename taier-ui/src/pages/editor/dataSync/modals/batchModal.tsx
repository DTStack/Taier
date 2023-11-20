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
import type { ModalProps } from 'antd';
import { Input,Modal } from 'antd';
import type { TextAreaProps } from 'antd/lib/input';

import { DATA_SOURCE_ENUM, HBASE_FIELD_TYPES,HDFS_FIELD_TYPES } from '@/constant';

const renderTypes = (sourceType?: DATA_SOURCE_ENUM) => {
    const types = sourceType === DATA_SOURCE_ENUM.HBASE ? HBASE_FIELD_TYPES : HDFS_FIELD_TYPES;
    const typeItems = types?.map((type: any) => <b key={type}>{type}, </b>);
    return <span style={{ wordBreak: 'break-all' }}>{typeItems}</span>;
};

interface IBatchModalProps
    extends Pick<ModalProps, 'title' | 'visible' | 'onCancel'>,
        Pick<TextAreaProps, 'placeholder'> {
    desc?: string;
    defaultValue?: string;
    sourceType?: DATA_SOURCE_ENUM;
    columnFamily?: string[];
    onOk?: (value: string) => void;
}

export default function BatchModal({
    title,
    desc,
    visible,
    placeholder,
    sourceType,
    columnFamily,
    defaultValue,
    onCancel,
    onOk,
}: IBatchModalProps) {
    const [value, setValue] = useState('');
    const isNotHBase = sourceType !== DATA_SOURCE_ENUM.HBASE;

    const handleSubmit = () => {
        onOk?.(value);
    };

    useEffect(() => {
        if (visible && defaultValue !== undefined) {
            setValue(defaultValue);
        }
    }, [visible]);

    return (
        <Modal title={title} onOk={handleSubmit} onCancel={onCancel} maskClosable={false} visible={visible}>
            <div>
                {isNotHBase ? '批量导入的语法格式（index 从 0 开始）：' : '批量添加的语法格式:'}
                <b style={{ color: 'rgb(255, 102, 0)' }}>
                    {desc && Object.prototype.toString.call(desc)?.slice(8, -1) === 'String'
                        ? desc.split(',').map((item: any) => <p key={item}>{item}</p>)
                        : desc}
                </b>
                <p>
                    常用数据类型（type)：
                    <span style={{ color: 'rgb(255, 102, 0)' }}>{renderTypes(sourceType)}</span>
                </p>
                {columnFamily ? (
                    <p>
                        已有列族：
                        <span style={{ color: 'rgb(255, 102, 0)' }}>{columnFamily?.map((col) => `${col},`)}</span>
                    </p>
                ) : (
                    ''
                )}
            </div>
            <br />
            <Input.TextArea
                rows={6}
                value={value}
                onChange={(e) => setValue(e.target.value)}
                placeholder={placeholder}
            />
        </Modal>
    );
}
