import * as React from 'react';
import { Form, Row, Col, Select, Button } from 'antd';
import styled from 'styled-components'

import GroupAPI from '../../../../api/group';

interface IState {
    groups: any[];
    tags: any[];
    result: any;
}

const FormItem = Form.Item;
const Option = Select.Option;

const IndexContainer = styled.div`
    background: rgba(36,145,247,0.10);
    border-radius: 0.75px;
    border-radius: 0.75px;
    padding: 0 10px;
    text-align: center;
    min-width: 200px;
    display: inline-block;
    height: 32px;
    margin-left: 8px;
`

const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 20 }
    }
}

export default class GroupPortrait extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        groups: [],
        tags: [],
        result: {}
    }

    componentDidMount () {
        this.getGroups();
    }

    getGroups = async () => {
        const res = await GroupAPI.getGroups();
        if (res.code === 1) {
            this.setState({
                groups: res.data
            })
        }
    }

    getTags = async () => {
        // TODO 获取标签列表
        const res = await GroupAPI.getGroups();
        if (res.code === 1) {
            this.setState({
                tags: res.data
            })
        }
    }

    startAnalyse = async () => {
        const res = await GroupAPI.getGroups();
        if (res.code === 1) {
            this.setState({
                groups: res.data
            })
        }
    }

    render () {
        const { groups, tags, result } = this.state;
        const filterContent = (
            <Form className="c-groupPortrait__form">
                <FormItem
                    label="选择群体A"
                    hasFeedback
                    required
                    {...formItemLayout}
                >
                    <Select
                        placeholder="请选择群体"
                        style={{ width: 200 }}
                    >
                        { groups && groups.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                    <IndexContainer>
                        <span>{result.groupB}个样本在当前时间内被标记</span>
                    </IndexContainer>
                </FormItem>
                <FormItem
                    label="选择群体B"
                    hasFeedback
                    {...formItemLayout}
                    required
                >
                    <Select
                        placeholder="请选择群体"
                        style={{ width: 200 }}
                    >
                        { groups && groups.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                    <IndexContainer>
                        <span>{result.groupA}个样本在当前时间内被标记</span>
                    </IndexContainer>
                </FormItem>
                <FormItem
                    label="对比分析标签"
                    hasFeedback
                    required
                    {...formItemLayout}
                >
                    <Select
                        mode={'multiple'}
                        placeholder="对比分析标签"
                        style={{ width: 200 }}
                    >
                        { tags && tags.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                </FormItem>
                <FormItem
                    wrapperCol={{
                        span: 3,
                        offset: 3
                    }}
                    label=""
                    hasFeedback
                >
                    <Button type="primary" onClick={this.startAnalyse}>开始分析</Button>
                </FormItem>
            </Form>
        );

        return (
            <div className="c-groupPortrait">
                <Row>
                    <Col span={22}>
                        { filterContent }
                    </Col>
                    <Col span={2}>
                        <IndexContainer>
                            <span>重叠样本量{result.repeat}</span>
                        </IndexContainer>
                    </Col>
                </Row>
                <Row gutter={20} className="c-groupPortrait__chart" type="flex" justify="space-between">
                    <Col><div className="c-groupPortrait__chart-item"></div></Col>
                    <Col><div className="c-groupPortrait__chart-item"></div></Col>
                    <Col><div className="c-groupPortrait__chart-item"></div></Col>
                </Row>
            </div>
        )
    }
}
