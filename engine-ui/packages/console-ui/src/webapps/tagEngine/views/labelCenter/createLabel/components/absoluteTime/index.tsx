import * as React from 'react';
import { Col, Row, Select, Icon, Tooltip, DatePicker } from 'antd';
import TagTypeOption from '../../../../../consts/tagTypeOption';
import moment from 'moment';
import './style.scss';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface IProps {
    tip?: string;
    value?: any;
    data?: any;
    onChange?: any;
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
        const { data, onChange } = this.props;
        onChange(Object.assign({}, data, { timeType: value, value: '', values: [] }))
    }
    onChangeRangePicker = (value, dateStrings) => {
        const { data, onChange } = this.props;
        onChange(Object.assign({}, data, { value: '', values: dateStrings }))
    }
    onChangeDatePicker = (value) => {
        const { data, onChange } = this.props;
        onChange(Object.assign({}, data, { value, values: [] }))
    }
    render () {
        const { tip, data } = this.props;
        const { values } = data;
        let rangDate = values.map(item => moment(item))
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
                        ) : (<DatePicker onChange={this.onChangeDatePicker} value={data.value} showTime format="YYYY-MM-DD HH:mm:ss" placeholder="Select Time" />)
                    }
                </Col>
                <Col>
                    <Tooltip placement="top" title={tip}>
                        <Icon type="question-circle-o" className="tip"/>
                    </Tooltip>
                </Col>
            </Row>
        );
    }
}
