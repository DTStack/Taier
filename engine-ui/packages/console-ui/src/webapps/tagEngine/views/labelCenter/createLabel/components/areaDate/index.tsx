import * as React from 'react';
import { Col, Row, Select, Icon, Tooltip, DatePicker } from 'antd';
import './style.scss';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface IProps {
    tip?: string;
}

interface IState {
    visible: boolean;
}

export default class AreaDate extends React.PureComponent<
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
        const { tip } = this.props
        return (
            <Row className="area-date-Row" type='flex' gutter={8}>
                <Col>
                    <Select defaultValue="大于等于" style={{ width: 80, marginRight: 20 }}>
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    <RangePicker
                        showTime
                        format="YYYY-MM-DD HH:mm:ss"
                    />
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
