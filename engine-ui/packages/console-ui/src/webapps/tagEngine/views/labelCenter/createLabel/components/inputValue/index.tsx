import * as React from 'react';
import { InputNumber, Col, Row, Input, Form } from 'antd';

interface IProps {
    type?: string;
    value?: any;
    data?: number;
    onChangeData?: any;
    getFieldDecorator?: any;
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
    componentDidMount () { }
    onChangeInputNumber = (value) => {
        this.props.onChangeData({ value: value })
    }
    onChangeInputValue = (e) => {
        const value = e.target.value;
        this.props.onChangeData({ value: value })
    }
    render () {
        const { data, type, getFieldDecorator } = this.props;
        let Component = type == 'number' ? (
            <InputNumber min={1} value={ data } onChange={this.onChangeInputNumber}/>
        ) : (
            <Input value={data} onChange={this.onChangeInputValue}/>
        )
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>

                    <Form.Item required={false}>
                        {
                            getFieldDecorator('value', {
                                rules: [{
                                    require: true,
                                    message: '请输入值'
                                }]
                            })(Component)
                        }
                    </Form.Item>
                </Col>
            </Row>
        );
    }
}
