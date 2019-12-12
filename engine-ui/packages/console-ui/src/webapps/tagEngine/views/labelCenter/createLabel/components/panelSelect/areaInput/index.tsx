import * as React from 'react';
import { InputNumber, Col, Row, Icon, Tooltip, Form } from 'antd';
import './style.scss';

interface IProps {
    tip?: string;
    leftText?: string;
    centerText?: string;
    rightText?: string;
    value?: any;
    data?: {
        rValue: number;
        lValue: number;
    };
    type?: string;
    form?: any;
    rowKey?: string;
    onChangeData?: any;
}

interface IState {
    visible: boolean;
    confirmDirty: boolean;
}

export default class AreaInput extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        visible: false,
        confirmDirty: false
    };
    componentDidMount () { }
    onChangeLvalue = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { lValue: value }))
    }
    onChangeRvalue = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { rValue: value }))
    }
    checkLeft = (rule, value, callback) => {
        const { form, rowKey, type } = this.props;
        let rValue = form.getFieldValue(rowKey + 'r');
        if (type == 'number') {
            if (value && rValue && value > rValue) {
                // form.validateFields(['confirm'], { force: true });
                // eslint-disable-next-line standard/no-callback-literal
                callback('起始数值要小于终止数值！');
            } else {
                callback();
            }
        } else {
            if (value && rValue && value < rValue) {
                // eslint-disable-next-line standard/no-callback-literal
                callback('起始数值要大于终止数值！');
            } else {
                callback();
            }
        }
    }
    checkRight = (rule, value, callback) => {
        const { form, rowKey, type } = this.props;
        if (type == 'number') {
            if (value && value < form.getFieldValue(rowKey + 'l')) {
                // eslint-disable-next-line standard/no-callback-literal
                callback('终止数值要大于起始数值！');
            } else {
                callback();
            }
        } else {
            if (value && value > form.getFieldValue(rowKey + 'l')) {
                // eslint-disable-next-line standard/no-callback-literal
                callback('终止数值要小于起始数值！');
            } else {
                callback();
            }
        }
    }
    render () {
        const { tip, leftText, centerText, rightText, data, rowKey, form } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Row className="area-input-Row" type='flex' align="middle" gutter={8}>
                <Col>
                    {
                        leftText
                    }
                </Col>
                <Col>
                    <Form.Item>
                        {
                            getFieldDecorator(rowKey + 'l', {
                                initialValue: data.lValue || null,
                                rules: [
                                    {
                                        required: true,
                                        message: '请输入值！'
                                    }, {
                                        validator: this.checkLeft
                                    }
                                ]
                            })(
                                <InputNumber min={1} onChange={this.onChangeLvalue}/>
                            )
                        }
                    </Form.Item>
                </Col>
                <Col>
                    {
                        centerText
                    }
                </Col>
                <Col>
                    <Form.Item>
                        {
                            getFieldDecorator(rowKey + 'r', {
                                initialValue: data.rValue || null,
                                rules: [
                                    {
                                        required: true,
                                        message: '请输入值！'
                                    }, {
                                        validator: this.checkRight
                                    }
                                ]
                            })(
                                <InputNumber min={1} onChange={this.onChangeRvalue}/>
                            )
                        }
                    </Form.Item>
                </Col>
                <Col>
                    {
                        rightText
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
