import * as React from 'react';
import { Col, Row, Select, DatePicker } from 'antd';
import TagTypeOption from '../../../../../consts/tagTypeOption';
import moment from 'moment';
import './style.scss';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface IProps {
    tip?: string;
    value?: any;
    data?: any;
    onChangeData?: any;
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
        console.log(dateStrings);
        onChangeData(Object.assign({}, data, { value: '', lValue: dateStrings[0], rValue: dateStrings[1] }))
    }
    onChangeDatePicker = (value) => {
        const { data, onChangeData } = this.props;
        onChangeData(Object.assign({}, data, { value, lValue: '', rValue: '' }))
    }
    render () {
        const { data } = this.props;
        const { lValue, rValue, value } = data;
        let rangDate: any = lValue && rValue ? [moment(lValue), moment(rValue)] : [];
        return (
            <Row className="absoluteTime" type='flex' gutter={8}>
                <Col>
                    <Select value={data.timeType} onChange={this.onChangeSelect} style={{ width: 80, marginRight: 20 }}>
                        {
                            TagTypeOption['OP_ABSOLUTE_TIME'].map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)
                        }
                    </Select>
                </Col>
                <Col>
                    {
                        data.timeType == 'OP_BETWEEN' ? (
                            <RangePicker
                                showTime
                                value={rangDate}
                                onChange={this.onChangeRangePicker}
                                format="YYYY-MM-DD HH:mm:ss"
                            />
                        ) : (<DatePicker onChange={this.onChangeDatePicker} value={moment(value)} showTime format="YYYY-MM-DD HH:mm:ss" placeholder="Select Time" />)
                    }
                </Col>
            </Row>
        );
    }
}
