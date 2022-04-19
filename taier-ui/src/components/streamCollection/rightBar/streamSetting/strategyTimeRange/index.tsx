import React from 'react';
import { message } from 'antd';
import { DeleteOutlined, PlusCircleOutlined } from '@ant-design/icons';
import { cloneDeep } from 'lodash';
import './style.scss';
import WrapTimePicker from '../wrapTimePicker';

const initItem = {
    id: 0,
    startDate: '',
    endDate: ''
};

interface IProps {
    value: any[];
    onChange?: Function;
    disabled?: boolean;
    isTimeDisabled?: boolean;// 是否有后一项时间必须大于前一项的限制
}

export default class StrategyTimeRange extends React.PureComponent<IProps, any> {
    handleChange = (list: any) => {
        const { onChange } = this.props;
        if (onChange) {
            onChange(list);
        }
    }

    // 时间范围选择
    onTimePicker = (index: number, timeRange: any) => {
        const { value } = this.props;
        const newList: any = cloneDeep(value);
        const item = Object.assign({}, newList[index], timeRange);
        newList.splice(index, 1, item);
        this.handleChange(newList);
    }

    // 添加控件
    handleAdd = () => {
        const { value } = this.props;
        const newList: any = cloneDeep(value);
        newList.push({
            ...initItem,
            id: value.length
        });
        this.handleChange(newList);
    }

    // 删除控件
    handleMinus = (index: number) => {
        const { value } = this.props;
        if (value.length === 1) {
            message.error('至少有一条时间范围');
            return;
        }
        const newList = cloneDeep(value);
        newList.splice(index, 1);
        this.handleChange(newList);
    }

    render () {
        const { value, disabled, isTimeDisabled } = this.props;
        return (
            <div className="strategy-time-range">
                <div className="strategy-time-range_container">
                    {value.map((item: any, index: number) => {
                        return (
                            <div key={item.id} className="strategy-time-range_item">
                                <WrapTimePicker
                                    isTimeDisabled= {isTimeDisabled}
                                    value={item}
                                    disabled={disabled}
                                    onChange={(timeRange: any) => this.onTimePicker(index, timeRange)}
                                />
                                {!disabled && <DeleteOutlined
                                    className="strategy-time-range_btn-delete font-light-gray"
                                    onClick={() => this.handleMinus(index)}
                                />}
                            </div>
                        )
                    })}
                </div>
                {!disabled && <a className="strategy-time-range_btn-add" onClick={this.handleAdd}><PlusCircleOutlined />&nbsp;添加时间范围</a>}
            </div>
        )
    }
}
