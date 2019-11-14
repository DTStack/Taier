import * as React from 'react';
import { Input, Col, Row, Select, InputNumber } from 'antd';
import './style.scss';
const { Option } = Select;
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
        } else {
            return <Input />
        }
    }
    render () {
        const { extra } = this.props
        return (
            <Row className="select-label-Row" type='flex' gutter={16}>
                <Col>
                    <Select defaultValue="lucy" style={{ width: 120 }}>
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    {
                        this.renderTypeFilter('select')
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
