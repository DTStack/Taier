import React from 'react';
import { TimePicker } from 'antd';
import { cloneDeep } from 'lodash';
import moment from 'moment';

const timeFormat = 'HH:mm'
const hours = Array.from(Array(24), (v, k) => k);
const minutes = Array.from(Array(60), (v, k) => k);

class WrapTimePicker extends React.Component<any, any> {
    onTimeChange = (time: any, timeString: string, type: string) => {
        const { value = {}, isTimeDisabled = false } = this.props;
        let newValue = cloneDeep(value)
        if (isTimeDisabled) {
            const endDate = newValue.endDate
            const startDate = newValue.startDate
            if (type === 'startDate') { // 特殊操作（直接勾选另外一项的分钟选项，小时选项会默认定位到当前时间，造成错误）重置输入框值处理
                if (endDate) {
                    const diff = moment(timeString, 'HH:mm').diff(moment(endDate, 'HH:mm'))
                    const timeSubtract = moment.duration(diff).minutes()
                    if (timeSubtract > 0) {
                        newValue.endDate = ''
                    }
                }
            } else {
                if (startDate) {
                    const diff = moment(timeString, 'HH:mm').diff(moment(startDate, 'HH:mm'))
                    const timeSubtract = moment.duration(diff).minutes()
                    if (timeSubtract < 0) {
                        newValue.startDate = ''
                    }
                }
            }
        }
        this.timeEval(newValue, timeString, type)
    }
    /**
     * 时间赋值
     */
    timeEval (newValue: any, timeString: string, type: string) {
        newValue = {
            ...newValue,
            [type]: timeString
        }
        this.props.onChange({ ...newValue })
    }
    /**
     * 可选时间范围处理（小时）
     * @param type
     * @returns 可选数组
     */
    disabledHours = (type: string) => {
        const { value = {}, isTimeDisabled = false } = this.props;
        if (!isTimeDisabled) return []

        if (type === 'start') {
            if (value.endDate) {
                const minHours = moment.duration(value.endDate).hours()
                return hours.filter((item) => item > minHours)
            } else {
                return []
            }
        } else {
            if (value.startDate) {
                const maxHours = moment.duration(value.startDate).hours()
                return hours.filter((item) => item < maxHours)
            } else {
                return []
            }
        }
    }
    /**
     * 可选时间范围处理（分钟
     * @param h
     * @param type
     * @returns 可选数组
     */
    disabledMinutes = (h: any, type: string) => {
        const { value = {}, isTimeDisabled = false } = this.props;
        if (!isTimeDisabled) return []
        if (type === 'start') {
            const { endDate } = value
            if (endDate && h === moment.duration(endDate).hours()) {
                const minMinutes = moment.duration(endDate).minutes() - 1;
                return minutes.filter((item) => item > minMinutes)
            } else {
                return []
            }
        } else {
            const { startDate } = value
            if (startDate && h === moment.duration(startDate).hours()) {
                const maxMinutes = moment.duration(startDate).minutes() + 1;
                return minutes.filter((item) => item < maxMinutes)
            } else {
                return []
            }
        }
    }

    render () {
        const { value = {}, disabled } = this.props
        const { startDate = '', endDate = '' } = value
        return (
            <>
                <TimePicker
                    style={{ width: 140, marginRight: 14 }}
                    placeholder='开始时间'
                    value={startDate ? moment(startDate, timeFormat) as any : null}
                    onChange={(time: any, timeString: string) => this.onTimeChange(time, timeString, 'startDate')}
                    format={timeFormat}
                    disabled={disabled}
                    disabledHours={() => this.disabledHours('start')}
                    disabledMinutes={(value) => this.disabledMinutes(value, 'start')}
                />
                至
                <TimePicker
                    style={{ width: 140, marginLeft: 14 }}
                    placeholder='结束时间'
                    value={endDate ? moment(endDate, timeFormat) as any : null}
                    onChange={(time: any, timeString: string) => this.onTimeChange(time, timeString, 'endDate')}
                    disabledHours={() => this.disabledHours('end')}
                    disabledMinutes={(value) => this.disabledMinutes(value, 'end')}
                    format={timeFormat}
                    disabled={disabled} />
            </>
        )
    }
}
export default WrapTimePicker;
