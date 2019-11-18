import * as React from 'react';
import { Input, Col, Row, Select, InputNumber, DatePicker } from 'antd';
import './style.scss';
const { Option } = Select;
const { RangePicker } = DatePicker;
interface IProps {
    extra?: any;
    data: any;
}

interface IState {
    name: string;
}

export default class SelectLabelRow extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        name: ''
    };
    componentDidMount () {}
    renderTypeFilter = (type) => {
        if (type == 'select') {
            return (
                <Select defaultValue="lucy" style={{ width: 120 }}>
                    <Option value="lucy">Lucy</Option>
                </Select>
            )
        } else if (type == 'area-input') {
            return <div><InputNumber min={1} defaultValue={3} />-<InputNumber min={1} defaultValue={3} /></div>
        } else if (type === 'input') {
            return <Input />
        } else if (type === 'date') {
            return <DatePicker
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="Select Time"
            />
        } else if (type === 'area-date') {
            return <div>
                <Select defaultValue="大于等于" style={{ width: 80, marginRight: 20 }}>
                    <Option value="lucy">Lucy</Option>
                </Select>
                <RangePicker
                    showTime
                    format="YYYY-MM-DD HH:mm:ss"
                />
            </div>
        } else if (type === 'input') {
            return <Input />
        }
    }
    render () {
        const { extra } = this.props
        return (
            <Row className="select-label-Row" type='flex' gutter={16}>
                <Col>
                    <Select defaultValue="lucy" style={{ width: 100 }}>
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    <Select defaultValue="lucy" style={{ width: 100 }}>
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    {
                        this.renderTypeFilter('area-date')
                    }
                </Col>
                <Col>
                    {
                        extra
                    }
                </Col>
            </Row>
        );
    }
}
