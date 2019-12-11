import * as React from 'react';
import { Col, Row, Select, DatePicker, Form } from 'antd';
import TagTypeOption from '../../../../../../consts/tagTypeOption';
import moment from 'moment';
import './style.scss';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface IProps {
    tip?: string;
    value?: any;
    data?: any;
    onChangeData?: any;
    form?: any;
    rowKey?: string;
}

interface IState {
    visible: boolean;
}

export default class AbsoluteTime extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        visible: false
    };
    componentDidMount () { }
    onChangeSelect = (value) => {
        const { data, onChangeData } = this.props;
        onChangeData(Object.assign({}, data, { timeType: value, value: '', values: [] }))
    }
    onChangeRangePicker = (value, dateStrings) => {
        const { data, onChangeData } = this.props;
        onChangeData(Object.assign({}, data, { value: '', lValue: dateStrings[0], rValue: dateStrings[1] }))
    }
    onChangeDatePicker = (value) => {
        const { data, onChangeData } = this.props;
        onChangeData(Object.assign({}, data, { value, lValue: '', rValue: '' }))
    }
    render () {
        const { data, form, rowKey } = this.props;
        const { getFieldDecorator } = form;
        const { lValue, rValue, value, timeType } = data;
        let rangeDate = lValue && rValue ? [moment(lValue), moment(rValue)] : [];
        const rangeConfig = {
            initialValue: rangeDate,
            rules: [{ type: timeType == 'OP_BETWEEN' ? 'array' : 'object', required: true, message: '请选择时间!' }]
        };
        const dateConfig = {
            initialValue: value ? moment(value) : moment(),
            rules: [{ type: 'object', required: true, message: '请选择时间!' }]
        };
        return (
            <Row className="absoluteTime" type='flex' gutter={8} align="middle">
                <Col>
                    <Select value={timeType} onChange={this.onChangeSelect} style={{ width: 80, marginRight: 20 }}>
                        {
                            TagTypeOption['OP_ABSOLUTE_TIME'].map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)
                        }
                    </Select>
                </Col>
                <Col>
                    {
                        timeType == 'OP_BETWEEN' ? (
                            <Form.Item>
                                {
                                    getFieldDecorator(rowKey + '-rangePicker', rangeConfig)(
                                        <RangePicker
                                            showTime
                                            onChange={this.onChangeRangePicker}
                                            format="YYYY-MM-DD HH:mm:ss"
                                        />
                                    )}
                            </Form.Item>
                        ) : (
                            <Form.Item>
                                {
                                    getFieldDecorator(rowKey + '-datePicker', dateConfig)(
                                        <DatePicker onChange={this.onChangeDatePicker} showTime format="YYYY-MM-DD HH:mm:ss" placeholder="Select Time" />
                                    )}
                            </Form.Item>
                        )
                    }

                </Col>
            </Row>
        );
    }
}
