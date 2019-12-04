import * as React from 'react';
import { InputNumber, Col, Row, Input } from 'antd';

interface IProps {
    type?: string;
    value?: any;
    data?: number;
    onChangeData?: any;
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
        const { data, type } = this.props;
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>
                    {
                        type == 'number' ? (
                            <InputNumber min={1} disabled value={ data } onChange={this.onChangeInputNumber}/>
                        ) : (
                            <Input value={data} disabled onChange={this.onChangeInputValue}/>
                        )
                    }
                </Col>
            </Row>
        );
    }
}
