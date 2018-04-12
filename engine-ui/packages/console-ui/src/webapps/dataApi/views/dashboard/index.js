import React, { Component } from 'react'
import { connect } from 'react-redux';

import { Alert, Menu } from 'antd';
import { isEmpty, cloneDeep } from 'lodash';

import Resize from 'widgets/resize';
import TopCall from "./TopCall";
import TopFail from "./TopFail";
import ErrorDistributed from "./ErrorDistributed";

import { lineAreaChartOptions } from '../../consts';
import { dashBoardActions } from '../../actions/dashBoard';
import AdminDashboard from './AdminDashboard';
import UserDashboard from './UserDashboard';




// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const mapStateToProps = state => {
    const { dashBoard, user } = state;
    return { dashBoard, user }
};

const mapDispatchToProps = dispatch => ({
    chooseUserDate(topn,e) {
        dispatch(dashBoardActions.chooseUserDate(e.target.value,topn));
    },
    chooseAdminDate(topn,e) {
        dispatch(dashBoardActions.chooseAdminDate(e.target.value,topn));
    },
    getApiCallInfo(params,isAdmin,date){
        dispatch(dashBoardActions.getApiCallInfo(params,isAdmin,date))
    },
    getApprovedMsgCount(){
        dispatch(dashBoardActions.getApprovedMsgCount())
    },
    getUserCallTopN(topn,isAdmin,date){
        dispatch(dashBoardActions.getUserCallTopN({topn:topn,time:date},isAdmin,date))
    },
    getApiCallFailRateTopN(topn,isAdmin,date){
        dispatch(dashBoardActions.getApiCallFailRateTopN({topn:topn,time:date},isAdmin,date))
    },
    getApiSubscribe(date){
        dispatch(dashBoardActions.getApiSubscribe(date));
    },
    getApiCallErrorInfo(date){
        dispatch(dashBoardActions.getApiCallErrorInfo(date));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class Dashboard extends Component {
    state = {
        dashBoardView: ""
    }
    



    componentWillReceiveProps(nextProps) {
        let oldData = this.props.dashBoard.alarmTrend,
            newData = nextProps.dashBoard.alarmTrend;

        if (isEmpty(oldData) && !isEmpty(newData)) {
            this.initLineChart(newData)
        }
    }
    isAdmin(){
        return this.props.user.isAdmin || true;
    }
    getNowView() {
        const isAdmin = this.isAdmin();
        //假如不是管理员，则直接返回Myview视图
        if (!isAdmin) {
            return "MyView"
        }
        //假如是管理员，但是没有dashBoardView,则返回overview
        if (!this.state.dashBoardView) {
            return "overView"
        }
        //是管理员并且有dashBoardView
        return this.state.dashBoardView;
    }
    getDashBoardView() {
        if (this.getNowView() == "overView") {
            return <AdminDashboard {...this.props}></AdminDashboard>
        }
        return <UserDashboard {...this.props}></UserDashboard>
    }

    handleClick = (e) => {
        this.setState({
            dashBoardView: e.key,
        });
    }
    getMenuView() {
        const isAdmin = this.isAdmin();
        if (!isAdmin) {
            return null;
        }
        return (
            <Menu
            className="margin-0-20"
                onClick={this.handleClick}
                selectedKeys={[this.getNowView()]}
                mode="horizontal"
            >
                <Menu.Item key="overView">
                    市场概览
                </Menu.Item>
                <Menu.Item key="MyView">
                    我的Api
                </Menu.Item>
            </Menu>);
    }

    render() {
        const { children } = this.props
        return (
            <div className="dashboard nobackground">
                {this.getMenuView()}
                {this.getDashBoardView()}

            </div>
        )
    }
}



export default Dashboard
