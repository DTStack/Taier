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
    form?: any;
    rowKey?: string;
    onChangeData?: any;
}

interface IState {
    visible: boolean;
}

export default class AreaInput extends React.PureComponent<
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
    onChangeLvalue = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { lValue: value }))
    }
    onChangeRvalue = (value) => {
        const { data } = this.props;
        this.props.onChangeData(Object.assign({}, data, { rValue: value }))
    }
    render () {
        const { tip, leftText, centerText, rightText, data, rowKey, form } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
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
