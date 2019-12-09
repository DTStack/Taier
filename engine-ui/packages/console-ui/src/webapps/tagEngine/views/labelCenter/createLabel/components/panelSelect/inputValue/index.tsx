import * as React from 'react';
import { InputNumber, Col, Row, Input, Form } from 'antd';

interface IProps {
    type?: string;
    value?: any;
    data?: number;
    onChangeData?: any;
    form?: any;
    rowKey?: string;
}

interface IState {
    visible: boolean;
}

export default class InputValue extends React.PureComponent<
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
        this.props.onChangeData({ value: value })
    }
    onChangeInputValue = (e) => {
        const value = e.target.value;
        this.props.onChangeData({ value: value })
    }
    render () {
        const { data, type, form, rowKey } = this.props;
        const { getFieldDecorator } = form;

        let Component = type == 'number' ? (
            <InputNumber min={1} onChange={this.onChangeInputNumber}/>
        ) : (
            <Input onChange={this.onChangeInputValue}/>
        )
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>
                    <Form.Item>
                        {
                            getFieldDecorator(rowKey, {
                                initialValue: data,
                                rules: [
                                    {
                                        required: true,
                                        message: '请输入值！'
                                    }
                                ]
                            })(Component)
                        }
                    </Form.Item>
                </Col>
            </Row>
        );
    }
}
