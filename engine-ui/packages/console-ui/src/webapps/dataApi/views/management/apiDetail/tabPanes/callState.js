import React, { Component } from 'react';
import { Col, Row } from 'antd';

import Resize from 'widgets/resize';
import { connect } from 'react-redux';
import { doubleLineAreaChartOptions } from '../../../../consts';
import { apiManageActions } from '../../../../actions/apiManage';
import { mineActions } from '../../../../actions/mine';
import { cloneDeep } from 'lodash'
import utils from 'utils'
import ManageTopCall from './topCall'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const mapStateToProps = state => {
    const { user, apiMarket, apiManage } = state;
    return { apiMarket, apiManage, user }
};

const mapDispatchToProps = dispatch => ({
    getApiCallUserRankList (apiId, time) {
        return dispatch(
            apiManageActions.getApiCallUserRankList({
                apiId: apiId,
                time: time
            })
        )
    },
    getApiCallInfo (apiId, time) {
        return dispatch(
            mineActions.getApiCallInfo({
                apiId: apiId,
                time: time,
                useAdmin: true
            })
        )
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class ApiManageCallState extends Component {
    state = {
        topCallUser: '',
        failPercent: '',
        callCount: '',
        callList: [],
        topCallList: [],
        apiId: '',
        dateType: ''
    }
    componentDidMount () {
        this.setState({
            apiId: this.props.apiId,
            dateType: this.props.dateType

        }, () => {
            this.getApiCallUserRankList();
            this.getApiCallInfoList();
        })
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.state.apiId != nextProps.apiId || this.state.dateType != nextProps.dateType) {
            this.setState({
                apiId: nextProps.apiId,
                dateType: nextProps.dateType

            }, () => {
                this.getApiCallUserRankList();
                this.getApiCallInfoList();
            })
        }
    }
    getApiCallInfoList () {
        let apiId = this.state.apiId;
        let time = this.state.dateType;

        if (!apiId || !time) {
            return;
        }
        this.props.getApiCallInfo(apiId, time)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            callList: res.data.infoList,
                            topCallUser: res.data.topCallUserName,
                            failPercent: res.data.failRate,
                            callCount: res.data.callCount
                        }, () => {
                            this.initLineChart();
                        })
                    }
                }
            )
    }
    getApiCallUserRankList () {
        let apiId = this.state.apiId;
        let time = this.state.dateType;

        if (!apiId || !time) {
            return;
        }
        this.props.getApiCallUserRankList(apiId, time)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            topCallList: res.data
                        })
                    }
                }
            )
    }
    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize()
    }
    initLineChart () {
        let chartData = this.state.callList
        let callCountDate = [];
        let failCountDate = [];
        let times = [];
        for (let i = 0; i < chartData.length; i++) {
            callCountDate.push(chartData[i].callCount)
            failCountDate.push(chartData[i].failRate)
            switch (this.props.dateType) {
                case '1':
                    times.push(utils.formatHours(chartData[i].time));
                    break;
                case '7':
                    times.push(utils.formatDateHours(chartData[i].time));
                    break;
                case '30':
                    times.push(utils.formatDate(chartData[i].time));
                    break;
                case '-1':
                    times.push(utils.formatDate(chartData[i].time));
                    break;
            }
        }
        let myChart = echarts.init(document.getElementById('manageApiDetail'));
        const option = cloneDeep(doubleLineAreaChartOptions);
        option.tooltip.formatter = function (params) {
            var relVal = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
                let unit = '次'
                if (params[i].seriesName == '失败率') {
                    unit = '%'
                }
                relVal += '<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>' + params[i].seriesName + ' : ' + params[i].value + unit;
            }
            return relVal;
        }
        option.series = [{
            symbol: 'none',
            name: '调用次数',
            data: callCountDate,
            type: 'line',
            smooth: true,
            itemStyle: {
                normal: {
                    color: '#1C86EE'
                }
            }
        }, {
            symbol: 'none',
            name: '失败率',
            data: failCountDate,
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            itemStyle: {
                normal: {
                    color: '#EE0000'
                }
            }
        }];
        option.xAxis[0].data = times;
        option.grid.left = 40
        option.grid.bottom = 10

        console.log(option)
        // 绘制图表
        myChart.setOption(option);
        this.setState({ lineChart: myChart })
    }
    getDateText () {
        switch (this.props.dateType) {
            case '1':
                return '24小时';
            case '7':
                return '7天';
            case '30':
                return '30天';
            case '-1':
                return '历史以来';
            default:
                return '';
        }
    }
    render () {
        return (
            <div style={{ paddingLeft: '20px', paddingRight: '20px' }}>
                <Row>
                    <Col span={16}>
                        <Row gutter={100} style={{ paddingLeft: '30px' }} className="m-count padding-l20 height-callstate-item">
                            <Col span={6}>
                                <section className="m-count-section margin-t20" style={{ width: 150 }}>
                                    <span className="m-count-title text-left">{this.getDateText()}累计调用</span>
                                    <span className="m-count-content font-black text-left">{this.state.callCount || 0}<span style={{ fontSize: 12 }}>次</span></span>
                                </section>
                            </Col>
                            <Col span={4}>
                                <section className="m-count-section margin-t20" style={{ width: 100 }}>
                                    <span className="m-count-title text-left">{this.getDateText()}失败率</span>
                                    <span className="m-count-content font-red text-left">{this.state.failPercent || 0}<span style={{ fontSize: 12 }}>%</span></span>
                                </section>
                            </Col>
                            {/* <Col span={10}>
                                <section className="m-count-section margin-t20" style={{ width: 150 }}>
                                    <span className="m-count-title text-left">TOP调用用户 </span>
                                    <span className="m-count-content font-black text-left">{this.state.topCallUser || '---'}</span>
                                </section>
                            </Col> */}
                        </Row>
                        <Resize onResize={this.resize}>
                            <article id="manageApiDetail" style={{ width: '100%', height: '250px' }} />
                        </Resize>
                    </Col>
                    <Col span={8} style={{ paddingTop: '15px' }}>
                        <p style={{ fontWeight: 'bold', lineHeight: 1, fontSize: '14px' }}>排行榜</p>
                        <ManageTopCall data={this.state.topCallList}></ManageTopCall>
                    </Col>
                </Row>

            </div>
        )
    }
}
export default ApiManageCallState;
