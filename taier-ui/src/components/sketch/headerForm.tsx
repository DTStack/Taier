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

import { useContext } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import type { InputProps, RadioGroupProps,SelectProps } from 'antd';
import { DatePicker, Form, Input, Radio,Select, Tooltip } from 'antd';
import type { DatePickerProps, RangePickerProps } from 'antd/lib/date-picker';
import classnames from 'classnames';

import Context from '@/context';
import type { ISlotItemProps } from '.';
import './headerForm.scss';

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const { Option } = Select;

// 内置任务名称搜索
export const InputItem = ({ formItemProps = {}, slotProps = {} }: Partial<ISlotItemProps<string, InputProps>>) => {
    const { style: inputStyle, ...restInputProps } = slotProps;

    return (
        <FormItem<string> name="name" {...formItemProps}>
            <Input
                addonAfter={null}
                placeholder="按任务名称搜索"
                style={{ width: 210, ...inputStyle }}
                suffix={<SearchOutlined style={{ cursor: 'pointer' }} />}
                {...restInputProps}
            />
        </FormItem>
    );
};

export type SearchType = 'fuzzy' | 'precise' | 'front' | 'tail';

const SEARCH_TYPE = [
    {
        key: 'caseSensitive',
        imgSrc: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABmJLR0QA/wD/AP+gvaeTAAABhElEQVRYhe2VPUvDQByHf5cmrRolQyHtYJ18gy4O4qSDg64i2II4SAvFNoWgX8ShQ64RBxFd6hdwc1QXQRCEirgo7WAXhxJJ079LC+LiRQWH3jPey8PD3cEBEolEMugw0YWc8zqAqRDuh1KpNB0+adAQvoI+nPM9APuMsblisXj72wD1B3vyRHQOIAdg90ucGQTBsGmaz9lsNhCRhToBzvkCgFNVVVc7nc617/sp27bfe3MHAJaJqMkYM3zfX7Rt++07pxImAEAewHGhUHgCcK9p2hoA1Gq1KBE9xuPxtGVZS4yxV03TVkSEwgGu644A2AQwX61WXQB6LwiZTMYHYLRarUvHca6IKA1gTMQr/AaIaANAnYhOiAiMsQsiOnRdd8JxnHFFUbYURZlsNBpBIpG4EfWGCcgTkWtZ1ll/jHO+3u12twEcARgNgoAnk8kUAJWI/jZAVdWcYRgvn8c8z9uJRqND5XK5WalUZmOx2Ey73b6LRCKaruueqPtfkX+BRCKRfAAM043+Rp32BgAAAABJRU5ErkJggg==',
        tip: '区分大小写匹配',
    },
    {
        key: 'precise',
        imgSrc: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAAwFBMVEUAAAD///+AgICioqKSkpKcnJyUlJSbm5uVlZWXl5eWlpaWlpaYmJiZmZmWlpaVlZWVlZWWlpaVlZWYmJiWlpaYmJiWlpaXl5eYmJiXl5eXl5eWlpaYmJiYmJiXl5eYmJiYmJiWlpaXl5eXl5eWlpaWlpaXl5eWlpaWlpaYmJiXl5eXl5eYmJiYmJiXl5eYmJiXl5eWlpaXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eIXQHmAAAAP3RSTlMAAQILDhITFxggIi4vMjM1Oj9BQ0RISVFUXWBmam9zdHmBhIWGjZaXoaOkp6qvvL7EyMzR2t3g4eTn6erz+PmloSGXAAAAAWJLR0QB/wIt3gAAAJlJREFUOMtjYBgF1AUqdvYowE4ZTYEdK4QW1oPQbLZoCuyhNJ82mgCKAh1TRgwdyHw2M31BIMUtxo5DgayqpAYDg5yBsjknFgX2JgzGPMxWLAxcAuL6kpb2WEzgtzEyspZmUNOVMZDCaoW6PAODiCGDqSi/BXYFChwMDExKzEJaihJ8OLwJBLyaOLxpx0YgJJXR40JpNH2SBgAKqha54oD4rgAAAABJRU5ErkJggg==',
        tip: '精确匹配',
    },
    {
        key: 'front',
        imgSrc: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABmJLR0QA/wD/AP+gvaeTAAAB20lEQVRYhe2QPYgTURSFz33zJtkogrI6hcUqIm5pobiVVlsq6SwEmyQwZMCfThYRXiXYSZjMzdOwYVER0gsWay2ynYiFICgoGIw2oilm8q7NBpYVMkEN28xX3nM553CAgoKCgoIpMPPLOI4X55mhcvSjvu/v38sCc2emAsYY1ev1Fnbfmfk1M2/NtYCIlIMgeDYaja7tCj8HoCQii51O5/ROrd/ve0mSnLDWHv4fBZ4qpcae5/WSJDlkjNHbUo2INgA8EpHa5D+O42PD4fAtgA3n3CtmNv9UAMAZEbngnHtHRO+DILhnrd0H4DIRPVFKPQZwpdVqlQFAa30EwK0ois5rrVcB3JxmrqeJACAiz5VSZSK6GIbhLwBg5qsAlIjc3n5b8H2/CqCfZdlXz/PuMvNalmUZgAPT/HMXIKImgA8isrbjXCeiB865TefcJoB1IqoDgFLqDoCtZrO5QkQ38vxzF1BKuTAMa8YYBQDtdvukiJwtlUqX6vX6DwDodrsv0jT9ZK1dGo/Hb0TkOjMfFJHjANxU/7wCE4wxE6MvRLQ8CQeARqPx3Tm3XKlUvkVRdN/zvKpz7uFgMKhqrU/NmvEHzPzRWrv01wYzkLfA5zRNf86zQEFBQcGe8xvNVbTHyF7uqwAAAABJRU5ErkJggg==',
        tip: '头部匹配',
    },
    {
        key: 'tail',
        imgSrc: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABmJLR0QA/wD/AP+gvaeTAAAB9klEQVRYhe2UP4gTQRjF37fJJqcRDw0kEUTwDygIIgh2NoIoWFwjKU5QDIRjJhxoddptY32wxcyuWEi4QnK1eJ1a2HqNIAh3V6jkApcyISCzz0YhitmcSqz2B9PM+2bem2+GATIyMjIyZkwURctRFC1P0vOzDkCynKZ7sw4wjakBrLUPrLU0xlz4k43b7XZpP3X7uYIGyQ0RaQC4/0u4inPuQKVS+Vyv192P+TiOjw0Gg01jzM1pm6d2wFp7GcCc7/sawGIYhsUx7QmAt57nrfX7/c0wDA8DgDHmiHNuRHJFRDYAXPzrAAAaANrNZnMHwAff9xcAoNPpFEhulcvl81rrKyKy5/v+NWPMoohsfR+rAI4CWEgzkElCHMcHkyTpAngtIrskLwHYU0rdICnW2scicp3kVxE5CWBFKfVsrENLAB6RfCMiO0qp4Hc+E98AyVsAPpJcIwkReUXyaRzHJ4wxxz3Pu+153plut+uq1eq78bXGmFMAHpK8KiJ30jow8QpINkjGWut1rfW6Uuq5iLxIkuQugE8ADjnnbK1WeykiPx1Ea73d6/VOa62308xTO5DP5+/Nz89/GZ8bjUZLhUJhrtVq7YZheK5YLJ4dDofvc7mcXyqVRuO1QRAk08z/C9bawFobTNJn/hWLSH/WHhkZGRn/xDfA/cJ9mTtA3QAAAABJRU5ErkJggg==',
        tip: '尾部匹配',
    },
];

const CheckList = ({
    options,
    value,
    onChange,
}: {
    options: SearchType[];
    value?: SearchType;
    onChange?: (value: SearchType) => void;
}) => {
    const handleClick = (item: (typeof SEARCH_TYPE)[number]) => {
        const nextValue = value === item.key ? 'fuzzy' : (item.key as SearchType);
        onChange?.(nextValue);
    };

    return (
        <div style={{ display: 'flex' }}>
            {options.map((option) => {
                const item = SEARCH_TYPE.find((type) => type.key === option);
                if (!item) return null;
                return (
                    <Tooltip key={item.key} title={item.tip} mouseEnterDelay={0.5}>
                        <div
                            className={classnames('suffix-item', value === item.key && 'actived')}
                            style={{
                                backgroundImage: `url(${item.imgSrc})`,
                            }}
                            onClick={() => handleClick(item)}
                        />
                    </Tooltip>
                );
            })}
        </div>
    );
};

export const InputWithConditionItem = ({
    formItemProps = {},
    slotProps = {},
    restProps = {},
}: Partial<
    ISlotItemProps<
        string,
        InputProps,
        {
            filterOptions?: SearchType[];
            onSuffixClick?: (value: SearchType) => void;
        }
    >
>) => {
    const { filterOptions = ['precise', 'front', 'tail'], onSuffixClick } = restProps;

    return (
        <FormItem<string> name="multipleName" {...formItemProps}>
            <Input
                placeholder="按任务名称搜索"
                className="dt-multiple"
                suffix={
                    <Form.Item<string> name="multipleNameSuffix" initialValue="fuzzy" noStyle>
                        <CheckList options={filterOptions} onChange={onSuffixClick} />
                    </Form.Item>
                }
                {...slotProps}
            />
        </FormItem>
    );
};

export const OwnerItem = ({
    formItemProps = {},
    slotProps = {},
}: Partial<ISlotItemProps<number, SelectProps<number>>>) => {
    const { personList } = useContext(Context);
    const { style: selectStyle, ...restSelectProps } = slotProps;

    return (
        <FormItem<number> label="操作人" name="owner" {...formItemProps}>
            <Select
                allowClear
                showSearch
                size="middle"
                className="dt-form-shadow-bg"
                style={{ width: 200, ...selectStyle }}
                placeholder="请选择操作人"
                optionFilterProp="userName"
                {...restSelectProps}
            >
                {personList.map((item) => (
                    <Option key={item.id} value={item.id}>
                        {item.userName}
                    </Option>
                ))}
            </Select>
        </FormItem>
    );
};

export const RangeItem = ({
    formItemProps = {},
    slotProps = {},
}: Partial<ISlotItemProps<number, RangePickerProps>>) => {
    return (
        <FormItem name="rangeDate" label="日期范围选择" {...formItemProps}>
            <RangePicker {...slotProps} />
        </FormItem>
    );
};

export const DatePickerItem = ({
    formItemProps = {},
    slotProps = {},
}: Partial<ISlotItemProps<number, DatePickerProps>>) => {
    return (
        <FormItem name="date" label="业务日期" {...formItemProps}>
            <DatePicker format="YYYY-MM-DD" placeholder="业务日期" style={{ width: 210 }} {...slotProps} />
        </FormItem>
    );
};

export const SelectItem = ({
    formItemProps = {},
    slotProps = {},
}: Partial<ISlotItemProps<number, SelectProps<string>>>) => {
    const { style, ...restSelectProps } = slotProps;
    return (
        <FormItem name="select" {...formItemProps}>
            <Select style={{ width: 150, ...style }} options={[]} {...restSelectProps} />
        </FormItem>
    );
};

export const RadioItem = ({ formItemProps = {}, slotProps = {} }: Partial<ISlotItemProps<number, RadioGroupProps>>) => {
    return (
        <FormItem name="radioGroup" {...formItemProps}>
            <Radio.Group optionType="button" {...slotProps} />
        </FormItem>
    );
};
