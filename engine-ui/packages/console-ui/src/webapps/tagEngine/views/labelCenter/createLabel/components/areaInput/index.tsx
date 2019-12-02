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
        const { tip, leftText, centerText, rightText, data } = this.props;
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>
                    {
                        leftText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} value={data ? data.lValue : null} onChange={this.onChangeLvalue}/>
                </Col>
                <Col>
                    {
                        centerText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} value={data ? data.rValue : null} onChange={this.onChangeRvalue}/>
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
