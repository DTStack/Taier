import * as React from 'react';
import { get, cloneDeep } from 'lodash';
import { Form, Row, Col, Select, Button } from 'antd';
import styled from 'styled-components'

import { updateComponentState } from 'funcs';
import { EChartBar } from 'widgets/echart';

import GroupAPI, { IGroupsAnalysis } from '../../../../api/group';
import { API } from '../../../../api/apiMap';
import { IQueryParams } from '../../../../model/comm';
import { IGroup } from '../../../../model/group';
import { defaultBarOption } from '../../../../comm/const';

interface IState {
    groups: any[];
    tags: any[];
    result: any;
    queryParams: { entityId: string } & IQueryParams ;
    formData: IGroupsAnalysis;
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
    color: #666666;
    margin-left: 8px;
`

const IndexTitle = styled.span`
    font-family: PingFangSC-Medium;
    font-size: 16px;
    color: #2491F7;
    text-align: right;
`

const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

export default class GroupPortrait extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        groups: [],
        tags: [],
        result: {},
        queryParams: {
            entityId: null,
            total: 0,
            search: null,
            current: 1,
            size: 1000,
            orders: [{
                asc: false,
                field: 'updateAt'
            }]
        },
        formData: {
            entityId: '',
            groupPojoIdList: [],
            tagGroupList: []
        }
    }

    static getDerivedStateFromProps (props, state: IState) {
        const entityId = get(props, 'router.params.entityId')
        if (entityId !== get(state, 'queryParams.entityId')) {
            const newState: IState = Object.assign({}, state);
            newState.formData.entityId = entityId;
            newState.queryParams.entityId = entityId;
            return newState;
        }
        return null
    }

    componentDidMount () {
        this.getTags('');
        this.getGroups();
    }

    getGroups = async () => {
        const { queryParams } = this.state;
        const res = await GroupAPI.getGroups(queryParams);
        if (res.code === 1) {
            this.setState({
                groups: res.data.contentList
            })
        }
    }

    getTags = async (query: string) => {
        const res = await API.getGroupTag({
            entityId: get(this.props, 'router.params.entityId', ''),
            current: 1,
            search: query
        });
        if (res.code === 1) {
            this.setState({
                tags: get(res, 'data.contentList', [])
            })
        }
    }

    onGroupSelect = (value, option) => {
        const { formData = { groupPojoIdList: [] } } = this.state;
        const { groupPojoIdList = [] } = formData;
        const newArr = groupPojoIdList.slice();
        if (newArr.findIndex((group: IGroup) => group.groupId === value) < 0) {
            newArr.push({
                groupId: value,
                groupName: option.props['data-name']
            });
            updateComponentState(this, {
                formData: {
                    groupPojoIdList: newArr
                }
            })
        }
    }

    onTagSelect = (value, option) => {
        const { formData = { tagGroupList: [] } } = this.state;
        const { tagGroupList = [] } = formData;
        const newArr = tagGroupList.slice();
        if (newArr.findIndex((tag) => tag.tagId === value) < 0) {
            newArr.push({
                tagId: value,
                tagName: option.props['data-name']
            });
            updateComponentState(this, {
                formData: {
                    tagGroupList: newArr
                }
            })
        }
    }

    startAnalyse = async () => {
        const { formData } = this.state;
        const res = await GroupAPI.analysisGroups(formData);
        if (res.code === 1) {
            this.setState({
                result: res.data
            })
        }
    }

    render () {
        const { groups = [], tags, result, formData = { groupPojoIdList: [], tagGroupList: [] } } = this.state;
        const { groupPojoIdList = [] } = formData;
        const groupOptions = groups && groups.map((o: IGroup) => {
            const disabled = groupPojoIdList.findIndex((group: IGroup) => group.groupId === o.groupId) > -1;
            return <Option key={o.groupId} value={o.groupId} title={o.groupName} disabled={disabled} data-name={o.groupName}>{o.groupName}</Option>
        });
        const options = cloneDeep(defaultBarOption);

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
                        style={{ width: 120 }}
                        onSelect={this.onGroupSelect}
                    >
                        { groupOptions }
                    </Select>
                    <IndexContainer>
                        <span>{result.groupB}个样本在当前时间内被标记</span>
                    </IndexContainer>
                    <IndexContainer style={{ position: 'absolute', height: '88px' }}>
                        <p>重叠样本量</p>
                        <IndexTitle>{result.repeat || 0}</IndexTitle>
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
                        style={{ width: 120 }}
                        onSelect={this.onGroupSelect}
                    >
                        { groupOptions }
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
                        style={{ width: 120 }}
                        onSearch={this.getTags}
                        onSelect={this.onTagSelect}
                    >
                        { tags && tags.map((o: any) => {
                            return <Option key={o.name} value={o.value} data-name={o.name}>{o.name}</Option>
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
                    <Col style={{ width: 600 }}>
                        { filterContent }
                    </Col>
                </Row>
                <Row gutter={20} className="c-groupPortrait__chart" type="flex" justify="space-between">
                    <Col><div id="JS_Chart_1" className="c-groupPortrait__chart-item"><EChartBar options={options}/></div></Col>
                    <Col><div id="JS_Chart_2" className="c-groupPortrait__chart-item"><EChartBar options={options}/></div></Col>
                    <Col><div id="JS_Chart_3" className="c-groupPortrait__chart-item"><EChartBar options={options}/></div></Col>
                </Row>
            </div>
        )
    }
}
