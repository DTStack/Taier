import React, { useEffect, useState } from 'react';
import { MinusOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Input, Tooltip } from 'antd';

import API from '@/api';
import { COMPARE_ENUM,TIME_RANGE, UNIT_WEIGHT } from './constants';
import './style.scss';

interface IProps {
    defaultValue?: string;
    value?: string;
    onRangeChange?: (timeStr: string) => void;
    onInputChange?: (timeStr: string) => void;
}
type RANGE_TYPE = 'sub' | 'add';

const GraphTimeRange: React.FC<IProps> = (props) => {
    const { value, defaultValue, onRangeChange, onInputChange } = props;
    const [time, setTime] = useState<string>(defaultValue || TIME_RANGE[0]);

    useEffect(() => {
        if (value !== undefined) {
            setTime(value);
        }
    }, [value]);

    // 拆分时间
    const breakUpTime = (timeStr: string) => {
        const num = parseInt(timeStr, 10);
        const unit = timeStr.replace(/\d/g, '').split('');
        return { num, unit };
    };

    /**
     * @param fTimeStr 当前时间
     * @param sTimeStr 比较的时间
     * @returns  -1: 当前时间小于比较时间, 0: 相等, 1: 大于
     */
    const timeCompare = (fTimeStr: string, sTimeStr: string) => {
        const { num: fNum, unit: fUnit } = breakUpTime(fTimeStr);
        const { num: sNum, unit: sUnit } = breakUpTime(sTimeStr);

        if (UNIT_WEIGHT[fUnit[0]] === UNIT_WEIGHT[sUnit[0]]) {
            if (fNum === sNum) {
                return fUnit.length === 1 ? COMPARE_ENUM.EQUAL : COMPARE_ENUM.GREATER;
            }
            return fNum < sNum ? COMPARE_ENUM.LESS : COMPARE_ENUM.GREATER;
        }
        return UNIT_WEIGHT[fUnit[0]] < UNIT_WEIGHT[sUnit[0]] ? COMPARE_ENUM.LESS : COMPARE_ENUM.GREATER;
    };

    // 获取下一个值
    const getNextTime = (type: RANGE_TYPE) => {
        for (let i = 0; i < TIME_RANGE.length; i += 1) {
            const compare = timeCompare(time, TIME_RANGE[i]);
            if (compare === COMPARE_ENUM.GREATER) {
                continue;
            }
            if (i === 0 && type === 'sub') {
                return undefined;
            }
            if (i === TIME_RANGE.length - 1 && compare === COMPARE_ENUM.EQUAL && type === 'add') {
                return undefined;
            }
            return type === 'add'
                ? compare === COMPARE_ENUM.LESS
                    ? TIME_RANGE[i]
                    : TIME_RANGE[i + 1]
                : TIME_RANGE[i - 1];
        }
    };

    // 范围变更
    const timeRangeChange = (type: RANGE_TYPE) => {
        const nextTime = getNextTime(type);
        handleChange(nextTime);
    };

    // 自定义时间区间
    const inputTimeChange = (e: any) => {
        setTime(e.target.value);
    };

    // input 框失去焦点
    const onInputBlur = (e: any) => {
        API.formatTimeSpan({
            timespan: e.target.value,
        })
            .then((res: any) => {
                const { data, code } = res;
                if (code === 1) {
                    const { correct, formatResult } = data;
                    if (correct) {
                        if (onInputChange) {
                            if (value === undefined) {
                                setTime(formatResult);
                            }
                            onInputChange(formatResult);
                        }
                    } else {
                        resetTimeRange();
                    }
                }
            })
            .catch(() => {
                resetTimeRange();
            });
    };

    // 重置时间
    const resetTimeRange = () => {
        setTime(value);
    };

    // onRangeChange
    const handleChange = (timeStr: string) => {
        if (!timeStr) {
            return;
        }
        if (onRangeChange) {
            onRangeChange(timeStr);
        }
        if (value === undefined) {
            setTime(timeStr);
        }
    };

    return (
        <div className="graph-time graph-time-range">
            <Button className="btn-prev" onClick={() => timeRangeChange('sub')}>
                <MinusOutlined />
            </Button>
            <Tooltip title={time}>
                <Input value={time} onChange={inputTimeChange} onBlur={onInputBlur} />
            </Tooltip>
            <Button className="btn-next" onClick={() => timeRangeChange('add')}>
                <PlusOutlined />
            </Button>
        </div>
    );
};
export default GraphTimeRange;
