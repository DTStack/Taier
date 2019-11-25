import * as React from 'react';
import { Input, Col, Row, Select, InputNumber, DatePicker, Form } from 'antd';
import './style.scss';
import MultiSelect from '../multiSelect';
import AreaInput from '../areaInput';
import AreaDate from '../areaDate';
const { Option } = Select;
interface IProps {
    extra?: any;
    data: any;
    getFieldDecorator: any;
}

interface IState {
    name: string;
}
const dataType = {
    CHARACTER: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '包含', value: 'OP_CONTAIN' }, { label: '不包含', value: 'OP_NOT_CONTAIN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }, { label: '存在', value: 'OP_EXIST' }, { label: '不存在', value: 'OP_NOT_ESIXT' }],
    TIME: [{ label: '绝对时间', value: 'OP_BETWEEN' }, { label: '相对当前时间点', value: 'OP_WITH_IN' }, { label: '相对当前时间区间', value: 'OP_WITH_IN_BETWEEN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }],
    TIME_ABS: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '小于', value: 'OP_LESS_THAN' }, { label: '小于等于', value: 'OP_LESS_THAN_EQUAL' }, { label: '大于', value: 'OP_GREATER_THAN' }, { label: '大于等于', value: 'OP_GREATER_THAN_EQUAL' }, { label: '区间', value: 'OP_BETWEEN' }],
    NUMBER: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '小于', value: 'OP_LESS_THAN' }, { label: '小于等于', value: 'OP_LESS_THAN_EQUAL' }, { label: '大于', value: 'OP_GREATER_THAN' }, { label: '大于等于', value: 'OP_GREATER_THAN_EQUAL' }, { label: '区间', value: 'OP_BETWEEN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }]

}
console.log(dataType);
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
    componentDidMount () { }
    renderTypeFilter = (type) => {
        const { getFieldDecorator, data } = this.props;
        if (type == 'select') {
            return (
                <Select defaultValue="lucy" style={{ width: 120 }}>
                    <Option value="lucy">Lucy</Option>
                </Select>
            )
        } else if (type == 'area-input') {
            return <AreaInput leftText="在 过去" centerText="天 到 过去" rightText="天 之内" tip="起始数值应大于终止数值。"/>
        } else if (type === 'inputNumber') {
            return <InputNumber />
        } else if (type === 'input') {
            return (<Form.Item>
                {
                    getFieldDecorator(data.key, {
                        rules: [{ required: true, message: '请输入值!' }]
                    })(
                        <Input />
                    )
                }
            </Form.Item>)
        } else if (type === 'date') {
            return <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" placeholder="Select Time" />
        } else if (type === 'area-date') {
            return <AreaDate/>
        } else if (type === 'input') {
            return <Input />
        } else if (type == 'multi-select') {
            return (<MultiSelect tip="提示选项为最近7天的属性关键词（最多展示 20 条），非所有关键词。可直接输入关键词，回车完成。"/>)
        }
    }
    render () {
        const { extra } = this.props;
        return (
            <Row className="select-label-Row" type='flex' gutter={16}>
                <Col>
                    <Select style={{ width: 100 }}>
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    <Select style={{ width: 100 }}>
                        {
                            dataType.CHARACTER.map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)
                        }
                        <Option value="lucy">Lucy</Option>
                    </Select>
                </Col>
                <Col>
                    {
                        this.renderTypeFilter('input')
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
