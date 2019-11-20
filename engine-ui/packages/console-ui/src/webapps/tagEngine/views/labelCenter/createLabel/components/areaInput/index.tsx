import * as React from 'react';
import { InputNumber, Col, Row, Icon, Tooltip } from 'antd';
import './style.scss';

interface IProps {
    tip?: string;
    leftText?: string;
    centerText?: string;
    rightText?: string;
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
    render () {
        const { tip, leftText, centerText, rightText } = this.props
        return (
            <Row className="area-input-Row" type='flex' gutter={8}>
                <Col>
                    {
                        leftText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} defaultValue={3} />
                </Col>
                <Col>
                    {
                        centerText
                    }
                </Col>
                <Col>
                    <InputNumber min={1} defaultValue={3} />
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
