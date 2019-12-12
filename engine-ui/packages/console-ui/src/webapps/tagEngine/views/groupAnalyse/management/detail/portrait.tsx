import * as React from 'react';
import { get, cloneDeep, isEmpty } from 'lodash';
import { Form, Row, Col, Select, Button, Spin } from 'antd';
import styled from 'styled-components'

import { updateComponentState } from 'funcs';
import * as EChart from 'widgets/echart';

import { API } from '../../../../api/apiMap';
import { IGroup } from '../../../../model/group';
import { IQueryParams } from '../../../../model/comm';
import { defaultBarOption } from '../../../../comm/const';
import GroupAPI, { IGroupsAnalysis } from '../../../../api/group';
import './style.scss'
interface IState {
    groups: any[];
    tags: any[];
    result: any;
    queryParams: { entityId: string } & IQueryParams;
    formData: IGroupsAnalysis;
    groupA: IGroup;
    groupB: IGroup;
    loading: boolean;
    groupOverlapData: any;
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
    height: 35px;
    line-height: 35px;
    box-sizing: border-box;
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
        sm: { span: 5 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

// const defaultBarOption = {
//     legend: {
//         data: []
//     },
//     title: {
//         text: ''
//     },
//     color: ['#2491F7', '#1BD7F7'],
//     xAxis: {
//         type: 'category',
//         data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
//     },
//     yAxis: {
//         type: 'value'
//     },
//     series: [{
//         data: [120, 200, 150, 80, 70, 110, 130],
//         type: 'bar'
//     }]
// };

export default class GroupPortrait extends React.PureComponent<any, IState> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        groups: [],
        tags: [],
        result: [],
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
        },
        groupA: {},
        groupB: {},
        groupOverlapData: {},
        loading: false
    }

    static getDerivedStateFromProps (props: any, state: IState) {
        const entityId = get(props, 'router.location.query.entityId')
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
            entityId: get(this.props, 'router.location.query.entityId', ''),
            current: 1,
            search: query
        });
        if (res.code === 1) {
            this.setState({
                tags: get(res, 'data', [])
            })
        }
    }

    onGroupASelect = (value: any, option: any) => {
        const { formData = { groupPojoIdList: [] } } = this.state;
        const { groupPojoIdList = [] } = formData;
        const group: IGroup = option.props['data-item'];
        const newArr = groupPojoIdList.slice();

        newArr[0] = {
            groupId: value,
            groupName: group.groupName
        }
        updateComponentState(this, {
            groupA: group,
            formData: {
                groupPojoIdList: newArr
            }
        }, () => {
            this.onGroupOverlapExtent();
        });
    }

    onGroupBSelect = (value: any, option: any) => {
        const { formData = { groupPojoIdList: [] } } = this.state;
        const { groupPojoIdList = [] } = formData;
        const group: IGroup = option.props['data-item'];
        const newArr = groupPojoIdList.slice();
        console.log('formData', formData)
        newArr[1] = {
            groupId: value,
            groupName: group.groupName
        }
        updateComponentState(this, {
            groupB: group,
            formData: {
                groupPojoIdList: newArr
            }
        }, () => {
            this.onGroupOverlapExtent();
        });
    }

    onGroupOverlapExtent = async () => {
        const { groupA, groupB, formData } = this.state;
        if (!isEmpty(groupA) && !isEmpty(groupB)) {
            const res = await GroupAPI.getGroupContactCount({
                entityId: formData.entityId,
                groupIdList: [groupA.groupId, groupB.groupId]
            })
            if (res.code === 1) {
                this.setState({
                    groupOverlapData: res.data
                })
            }
        }
    }

    onTagSelect = (value: any, option: any) => {
        const { formData = { tagGroupList: [] } } = this.state;
        const { tagGroupList = [] } = formData;
        if (tagGroupList.length < 20) {
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
    }
    onDeselect = (value: any) => {
        let { formData = { tagGroupList: [] } } = this.state;
        let { tagGroupList = [] } = formData;
        console.log('value111,', value);
        const newArr = tagGroupList.filter((item) => item.tagId !== value);
        updateComponentState(this, {
            formData: {
                tagGroupList: newArr
            }
        });
    }
    startAnalyse = async () => {
        const { formData } = this.state;
        this.setState({
            loading: true,
            result: []
        })
        const res = await GroupAPI.analysisGroups(formData);
        if (res.code === 1) {
            this.setState({
                result: res.data
            })
        }
        this.setState({
            loading: false
        })
    }

    renderAnalyseResult = () => {
        const { result } = this.state;
        const charts = result && result.map((chart: any, index: any) => {
            const options = cloneDeep(defaultBarOption);
            options.title.text = chart.title;
            options.legend.data = chart.legend;
            options.series = chart.series.map((o: any) => {
                o.type = 'bar';
                return o;
            });
            options.xAxis = {
                data: chart.xAxis,
                axisLine: { lineStyle: { type: 'solid', color: '#BABABA', width: 1 } },
                axisTick: { show: false },
                nameTextStyle: { color: '#666666' }
            }
            options.yAxis = {
                axisLine: { show: false },
                splitLine: { lineStyle: { type: 'dashed' } },
                axisTick: { show: false }
            }
            console.log('charts:' + index, options);
            return (
                <div key={`chart-${index}`} className="c-groupPortrait__chart-item">
                    <EChart.Bar options={options} />
                </div>
            )
        });
        return charts;
    }

    render () {
        const {
            groups = [], tags, groupA, groupB, groupOverlapData, formData
        } = this.state;
        const groupOptions = groups && groups.map((o: IGroup) => {
            const disabled = o.groupId === groupA.groupId || o.groupId === groupB.groupId;
            return <Option key={o.groupId} value={o.groupId} title={o.groupName} disabled={disabled} data-item={o}>{o.groupName}</Option>
        });
        const style = { height: 36, width: 359 };
        const filterContent = (
            <Form className="c-groupPortrait__form">
                <FormItem
                    label="选择群体A"
                    hasFeedback
                    required
                    {...formItemLayout}
                    style={{ marginBottom: 16 }}
                >
                    <Select
                        placeholder="请选择群体"
                        style={style}
                        onSelect={this.onGroupASelect}
                    >
                        {groupOptions}
                    </Select>
                    {
                        formData.groupPojoIdList[0] ? (
                            <IndexContainer>
                                <span className='number_font'>{groupA.groupDataCount}</span><span>个样本在当前时间内被标记</span>
                            </IndexContainer>
                        ) : ''
                    }
                    {
                        formData.groupPojoIdList[1] ? (
                            <IndexContainer style={{ position: 'absolute', height: '88px', padding: '10px 0' }}>
                                <p>重叠样本量</p>
                                <IndexTitle className='number_font'>{groupOverlapData.coincideNum}</IndexTitle>
                            </IndexContainer>
                        ) : ''
                    }

                </FormItem>
                <FormItem
                    label="选择群体B"
                    hasFeedback
                    {...formItemLayout}
                    style={{ marginBottom: 20 }}
                    required
                >
                    <Select
                        placeholder="请选择群体"
                        style={style}
                        onSelect={this.onGroupBSelect}
                    >
                        {groupOptions}
                    </Select>
                    {
                        formData.groupPojoIdList[1] ? (<IndexContainer>
                            <span className='number_font'>{groupB.groupDataCount}</span><span>个样本在当前时间内被标记</span>
                        </IndexContainer>) : ''
                    }

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
                        style={style}
                        onSearch={this.getTags}
                        onSelect={this.onTagSelect}
                        onDeselect={this.onDeselect}
                    >
                        {tags && tags.map((o: any) => {
                            return <Option key={o.tagId} value={o.tagId} data-name={o.tagName}>{o.tagName}</Option>
                        })}
                    </Select>
                </FormItem>
                <FormItem
                    wrapperCol={{
                        span: 3,
                        offset: 5
                    }}
                    label=""
                    hasFeedback
                >
                    <Button type="primary" onClick={this.startAnalyse} disabled={formData.groupPojoIdList[0] && formData.groupPojoIdList[1] ? false : true }>开始分析</Button>
                </FormItem>
            </Form>
        );

        return (
            <div className="c-groupPortrait">
                <Row>
                    <Col style={{ width: 1000 }}>
                        {filterContent}
                    </Col>
                </Row>

                <Spin tip="分析中..." spinning={this.state.loading}>
                    <Row className="c-groupPortrait__chart">
                        {this.renderAnalyseResult()}
                    </Row>
                </Spin>
            </div>
        )
    }
}
