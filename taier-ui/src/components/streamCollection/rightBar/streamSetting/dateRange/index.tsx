import React from 'react';
import { Spin } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import Calendar from 'rc-calendar';
import RangeCalendar from 'rc-calendar/lib/RangeCalendar';
import 'rc-calendar/assets/index.css';
import './style.scss';

type RangeType = 'single' | 'double';
interface IProps {
    value: string[];
    loading?: boolean;
    disabled?: boolean;
    type?: RangeType;
}
/**
 * @returns calendar 仅支持展示，不做修改
 */
const DateRange: React.FC<IProps> = (props) => {
    const { value, loading, disabled, type } = props;
    const rangeType = type || 'double';

    // 日期渲染
    const dateRender = (current: moment.Moment, monthCurrent: moment.Moment) => {
        const selected = value.includes(current.format('YYYYMMDD')) && current.month() === monthCurrent.month();
        return (
            <div className={classNames('rc-calendar-date', { 'rc-calendar-date-selected': selected })}>
                {current.date()}
            </div>
        );
    }

    return (
        <Spin spinning={!!loading}>
            {
                rangeType === 'single'
                    ? (
                        <Calendar
                            className={classNames('date-range_calendar', { 'disabled': disabled })}
                            showToday={false}
                            showDateInput={false}
                            showWeekNumber={false}
                            disabledDate={() => true}
                            dateRender={dateRender}
                        />
                    ) : (
                        <RangeCalendar
                            className={classNames('date-range_calendar', { 'disabled': disabled })}
                            hoverValue={[]}
                            selectedValue={[]}
                            showToday={false}
                            showDateInput={false}
                            showWeekNumber={false}
                            dateInputPlaceholder={['start', 'end']}
                            dateRender={dateRender}
                            disabledDate={() => true}
                        />
                    )
            }
        </Spin>
    )
}
export default DateRange;
