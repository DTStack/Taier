import * as React from 'react';
import { Col, Row, Select, Icon, Tooltip, InputNumber } from 'antd';
import TagTypeOption from '../../../../../consts/tagTypeOption';
import './style.scss';

const { Option } = Select;

interface IProps {
    tip?: string;
    value?: any;
    data?: {
        timeType: string;
        value: number;
    };
    onChange?: any;
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
        this.props.onChange(Object.assign({}, data, { value }))
    }
    onChangeSelect = (value) => {
        const { data } = this.props;
        this.props.onChange(Object.assign({}, data, { timeType: value }))
    }
    render () {
        const { tip, data } = this.props;
        return (
            <Row className="area-date-Row" type='flex' gutter={8}>
                <Col>
                    在
                </Col>
                <Col>
                    <InputNumber onChange={this.onChangeInputNumber} min={1} value={data.value} />
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
                <Col>
                    <Tooltip placement="top" title={tip}>
                        <Icon type="question-circle-o" className="tip" />
                    </Tooltip>
                </Col>
            </Row>
        );
    }
}
