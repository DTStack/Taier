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
import { InputNumber, Select } from 'antd';

import './index.scss';

const { Option } = Select;

interface ILifeCycleSelectProps {
    width: number | string;
    value?: number;
    onChange?: (value: number) => void;
}

const DEFAULT_DAYS = [3, 7, 30, 90, 365];

const OPTIONS = DEFAULT_DAYS.map((day) => ({
    renderLable: '天',
    label: `${day}天`,
    value: day,
})).concat({ renderLable: '自定义', label: '自定义', value: -1 });

export default function LifeCycleSelect({ width, value, onChange }: ILifeCycleSelectProps) {
    const [selectValue, setSelectValue] = useState(-1);
    const [readOnly, setReadOnly] = useState(true);
    const [open, setOpen] = useState(false);

    const handleClick = () => {
        if (readOnly) {
            setOpen(true);
        }
    };

    const handleChange = (v: number) => {
        setSelectValue(v);
        if (v === -1) {
            setReadOnly(false);
        } else {
            onChange?.(v);
            setReadOnly(true);
        }
    };

    useEffect(() => {
        const isCustomize = value !== undefined && DEFAULT_DAYS.includes(value);
        if (isCustomize) {
            setSelectValue(value);
        }

        setReadOnly(isCustomize);
    }, []);

    return (
        <>
            <InputNumber
                style={{ width: width || 200 }}
                placeholder="请输入生命周期"
                value={value}
                readOnly={readOnly}
                onClick={handleClick}
                min={0}
                onChange={(value) => onChange?.(value ?? 0)}
                addonAfter={
                    <Select
                        style={{ width: 100 }}
                        value={selectValue}
                        open={open}
                        optionLabelProp="label"
                        onChange={handleChange}
                        onDropdownVisibleChange={setOpen}
                    >
                        {OPTIONS.map((option) => (
                            <Option key={option.value} value={option.value} label={option.renderLable}>
                                {option.label}
                            </Option>
                        ))}
                    </Select>
                }
            />
        </>
    );
}
