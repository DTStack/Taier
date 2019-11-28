import * as React from 'react';
import { InputNumber, Col, Row, Icon, Tooltip } from 'antd';
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
    onChange?: any;
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
        this.props.onChange({ lValue: value });
    }
    onChangeRvalue = (value) => {
        this.props.onChange({ rValue: value });
    }
    render () {
        const { tip, leftText, centerText, rightText, value } = this.props;
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>
                    {
                        leftText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} value={value ? value.lValue : null} onChange={this.onChangeLvalue}/>
                </Col>
                <Col>
                    {
                        centerText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} value={value ? value.rValue : null} onChange={this.onChangeRvalue}/>
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
