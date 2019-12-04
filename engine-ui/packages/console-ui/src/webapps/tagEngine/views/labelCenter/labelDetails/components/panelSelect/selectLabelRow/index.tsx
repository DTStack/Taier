import * as React from 'react';
import { Col, Row, Select, Form } from 'antd';
import MultiSelect from '../multiSelect';
import AreaInput from '../areaInput';
import AbsoluteTime from '../absoluteTime';
import RelativeTime from '../relativeTime';
import InputValue from '../inputValue';
import TagTypeOption from '../../../../../../consts/tagTypeOption';
import './style.scss';

const { Option } = Select;
interface IProps {
    extra?: any;
    data: any;
    form?: any;
    getFieldDecorator: any;
    atomTagList: any[];
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
    renderTypeFilter = () => {
        const { getFieldDecorator, data } = this.props;
        const { tagId, dataType, type, timeType, lValue, rValue, value, values } = data;
        if (type == 'OP_HAVE' || type == 'OP_NOT') {
            return null
        }
        let Component;
        if (dataType == 'CHARACTER') { // 字符型
            if (type == 'OP_EQUAL' || type == 'OP_NOT_EQUAL') { // 如果是等于和不等于，属于区间范围
                Component = (<MultiSelect data={values} onChange={this.onChangeValue} tagId={tagId} tip="提示选项为最近7天的属性关键词（最多展示 20 条），非所有关键词。可直接输入关键词，回车完成。"/>)
            } else {
                Component = (<InputValue data={value} onChangeData={this.onChangeValue}/>)
            }
        } else if (dataType == 'TIME') { // 时间类型
            if (type == 'OP_ABSOLUTE_TIME') { // 绝对时间
                Component = <AbsoluteTime onChange={this.onChangeValue} data={{ timeType, value, values }}/>
            } else if (type == 'OP_RELATIVE_TIME') { // 相对时间点
                Component = (<RelativeTime onChange={this.onChangeValue} data={{ timeType, value }} tip=""/>)
            } else { // 相对时间区间
                Component = (<AreaInput onChange={this.onChangeValue} data={{ lValue, rValue }} leftText="在 过去" centerText="天 到 过去" rightText="天 之内" tip="起始数值应大于终止数值。"/>)
            }
        } else if (dataType == 'NUMBER') { // 数值型
            if (type == 'OP_EQUAL' || type == 'OP_NOT_EQUAL') { // 如果是等于和不等于，属于区间范围
                Component = (<MultiSelect onChange={this.onChangeValue} data={values} type="number" tagId={tagId} tip="可直接输入，回车完成"/>)
            } else if (type == 'OP_BETWEEN') {
                Component = (<AreaInput onChange={this.onChangeValue} data={{ lValue, rValue }} leftText="在 " centerText=" 于 " rightText="之间" tip="包含起始和结束值，起始数值应小于终止数值。"/>)
            } else {
                Component = (<InputValue data={value} type="number" onChangeData={this.onChangeValue}/>)
            }
        }
        return (<Form.Item>
            {
                getFieldDecorator(data.key, {
                    rules: [{
                        required: false, message: '请输入有效值!' },
                    {
                        validator: this.validateValue
                    }]
                })(Component)
            }
        </Form.Item>)
    }
    validateValue = (rule, rvalue, callback) => {
        const { data } = this.props;
        const { lValue, rValue, value, values } = data;
        if (lValue || rValue || value || values.length) {
            callback()
        } else {
            // eslint-disable-next-line standard/no-callback-literal
            callback('请输入有效值!')
        }
    }
    onChangeValue = (value) => {

    }
    render () {
        const { extra, atomTagList, data } = this.props;
        const { tagId, dataType, type } = data;
        return (
            <Row className="select-label-Row" type='flex' gutter={16}>
                <Col>
                    <Select showSearch value={tagId} disabled style={{ width: 100 }}>
                        {
                            atomTagList.map((item: any) => <Option key={item.tagId} value={item.tagId}>{item.tagName}</Option>)
                        }
                    </Select>
                </Col>
                <Col>
                    <Select style={{ width: 100 }} disabled value={type}>
                        {
                            TagTypeOption[dataType].map(item => <Option key={item.value} value={item.value}>{item.label}</Option>)
                        }
                    </Select>
                </Col>
                <Col>
                    {
                        this.renderTypeFilter()
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
