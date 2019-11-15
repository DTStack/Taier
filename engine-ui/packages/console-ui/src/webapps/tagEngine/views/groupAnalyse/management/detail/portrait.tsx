import * as React from 'react';
import { Form, Row, Col, Select, Button } from 'antd';
import styled from 'styled-components'

import { formItemLayout } from '../../../../comm/const';

interface IState {
    groupA: any[];
    groupB: any[];
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
`

export default class GroupPortrait extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        groupA: [],
        groupB: [],
        tags: [],
        result: {}
    }

    componentDidMount () {

    }

    startAnalyse = () => {

    }

    render () {
        const { groupA, groupB, tags, result } = this.state;
        const indexStyle = { width: '200px', textAlign: 'center' };
        const filterContent = (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="选择群体A"
                    hasFeedback
                >
                    <Select
                        placeholder="请选择群体"
                        style={{ width: 200 }}
                    >
                        { groupA && groupA.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                    <Col style={indexStyle}>
                        <IndexContainer>
                            <span>{result.groupB}个样本在当前时间内被标记</span>
                        </IndexContainer>
                    </Col>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择群体B"
                    hasFeedback
                >
                    <Select
                        placeholder="请选择群体"
                        style={{ width: 200 }}
                    >
                        { groupB && groupB.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                    <Col style={indexStyle}>
                        <IndexContainer>
                            <span>{result.groupA}个样本在当前时间内被标记</span>
                        </IndexContainer>
                    </Col>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="选择群体"
                    hasFeedback
                >
                    <Select
                        mode={'multiple'}
                        placeholder="请选择群体"
                        style={{ width: 200 }}
                    >
                        { tags && tags.map((o: any) => {
                            return <Option key={o.name} value={o.value}>{o.name}</Option>
                        })}
                    </Select>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="配置关联实体"
                    required
                    hasFeedback
                >
                    <Button type="primary" icon="plus" onClick={this.startAnalyse}>开始分析</Button>
                </FormItem>
            </Form>
        );

        return (
            <div className="c-groupPortrait">
                <Row>
                    <Col>
                        { filterContent }
                    </Col>
                    <Col>
                        <IndexContainer>
                            <span>重叠样本量{result.repeat}</span>
                        </IndexContainer>
                    </Col>
                </Row>
                <Row>
                    <Col></Col>
                </Row>
            </div>
        )
    }
}
