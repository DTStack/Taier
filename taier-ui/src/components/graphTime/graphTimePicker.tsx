import React from 'react';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import { Button, DatePicker } from 'antd';
import classNames from 'classnames';
import { cloneDeep } from 'lodash';
import moment from 'moment';

interface IProps {
    className?: string;
    style?: React.CSSProperties;
    timeRange: string;
    value: moment.Moment;
    onChange?: (endTime: moment.Moment) => void;
}

// 格式化时间区间，并获取时长
function getDuration(timeRange: string) {
    const duration = `P${timeRange.replace(/[^yd]*$/, 'T$&').toUpperCase()}`;
    return duration;
}

const GraphTimePicker: React.FC<IProps> = (props) => {
    const { timeRange, value, onChange, className, ...rest } = props;

    // 结束时间提前
    const endTimeAhead = () => {
        const time = cloneDeep(value);
        const endTime = time.subtract(getDuration(timeRange));
        handleEndTimeChange(endTime);
    };

    // 结束时间延后
    const endTimeDelay = () => {
        const time = cloneDeep(value);
        const newTime = time.add(getDuration(timeRange));
        handleEndTimeChange(newTime.isBefore(moment()) ? newTime : moment());
    };

    // 时间变更
    const handleEndTimeChange = (endTime: moment.Moment | null) => {
        if (onChange && endTime) {
            onChange(endTime);
        }
    };

    // 获取不可选时间范围
    const getDisabledTime = (date?: moment.Moment) => {
        const FORMAT_TYPE = 'YYYY-MM-DD';
        const current = moment();
        const { hours, minutes, seconds } = current.toObject();
        if (current.format(FORMAT_TYPE) !== date?.format(FORMAT_TYPE)) {
            return;
        }
        if (date?.hours() < hours) {
            return {
                disabledHours: () => getDisabledTimeRange(24, hours),
            };
        }
        if (date?.minutes() < minutes) {
            return {
                disabledHours: () => getDisabledTimeRange(24, hours),
                disabledMinutes: () => getDisabledTimeRange(60, minutes),
            };
        }
        return {
            disabledHours: () => getDisabledTimeRange(24, hours),
            disabledMinutes: () => getDisabledTimeRange(60, minutes),
            disabledSeconds: () => getDisabledTimeRange(60, seconds),
        };
        // 时间范围
        function getDisabledTimeRange(len: number, value: number) {
            return Array.from(new Array(len).keys()).filter((t: number) => t > value);
        }
    };

    return (
        <div className={classNames('graph-time graph-time-picker', { className })} {...rest}>
            <Button className="btn-prev" onClick={endTimeAhead}>
                <LeftOutlined />
            </Button>
            <DatePicker
                showTime
                placeholder="End Time"
                value={value}
                disabledDate={(current: moment.Moment) => current && current > moment()}
                disabledTime={getDisabledTime}
                onChange={(date) => {
                    handleEndTimeChange(date);
                }}
                onOk={(selectedTime: moment.Moment) => {
                    handleEndTimeChange(selectedTime);
                }}
            />
            <Button className="btn-next" onClick={endTimeDelay}>
                <RightOutlined />
            </Button>
        </div>
    );
};
export default GraphTimePicker;
