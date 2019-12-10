import * as React from 'react';
import { Col, Row, Select, InputNumber, Form } from 'antd';
import TagTypeOption from '../../../../../../consts/tagTypeOption';
import './style.scss';

const { Option } = Select;

interface IProps {
    tip?: string;
    value?: any;
    data?: {
        timeType: string;
        value: number;
    };
    onChangeData?: any;
    form?: any;
    rowKey?: string;
}

interface IState {
    visible: boolean;
}

export default class RelativeTime extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }
    state: IState = {
        visible: false
    };
    onChangeInputNumber = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { value }))
    }
    onChangeSelect = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { timeType: value }))
    }
    render () {
        const { data, rowKey, form } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Row className="area-date-Row" type='flex' align="middle" gutter={8}>
                <Col>
                    在
                </Col>
                <Col>
                    <Form.Item>
                        {
                            getFieldDecorator(rowKey, {
                                initialValue: data.value,
                                rules: [
                                    {
                                        required: true,
                                        message: '请输入值！'
                                    }
                                ]
                            })(
                                <InputNumber onChange={this.onChangeInputNumber} min={1} />
                            )
                        }
                    </Form.Item>
                </Col>
                <Col>
                    天
                </Col>
                <Col>
                    <Select value={data.timeType} onChange={this.onChangeSelect} style={{ width: 80, marginRight: 20 }}>
                        {
                            TagTypeOption['OP_RELATIVE_TIME'].map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)
                        }
                    </Select>
                </Col>
            </Row>
        );
    }
}
