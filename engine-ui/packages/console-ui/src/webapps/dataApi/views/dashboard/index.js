import React, { Component } from 'react'
import { connect } from 'react-redux';

import { Menu } from 'antd';
import { isEmpty } from 'lodash';

import { dashBoardActionType } from '../../consts/dashBoardActionType'
import { dashBoardActions } from '../../actions/dashBoard';
import AdminDashboard from './adminDashboard';
import UserDashboard from './userDashboard';

// 引入 ECharts 主模块
require('echarts/lib/echarts');
// 引入柱状图
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');

const mapStateToProps = state => {
    const { dashBoard, user, common } = state;
    return { dashBoard, user, common }
};

const mapDispatchToProps = dispatch => ({
    chooseUserDate (topn, e) {
        dispatch(dashBoardActions.chooseUserDate(e.target.value, topn));
    },
    chooseAdminDate (topn, e) {
        dispatch(dashBoardActions.chooseAdminDate(e.target.value, topn));
    },
    getApiCallInfo (params, isAdmin, date) {
        dispatch(dashBoardActions.getApiCallInfo(params, isAdmin, date))
    },
    getApprovedMsgCount () {
        dispatch(dashBoardActions.getApprovedMsgCount())
    },
    getUserCallTopN (topn, isAdmin, date) {
        dispatch(dashBoardActions.getUserCallTopN({ topn: topn, time: date }, isAdmin, date))
    },
    getApiCallFailRateTopN (topn, isAdmin, date) {
        dispatch(dashBoardActions.getApiCallFailRateTopN({ topn: topn, time: date }, isAdmin, date))
    },
    getApiSubscribe (date) {
        dispatch(dashBoardActions.getApiSubscribe(date));
    },
    getApiCallErrorInfo (date) {
        dispatch(dashBoardActions.getApiCallErrorInfo(date));
    },
    listApiCallNumTopNForManager (date) {
        dispatch(dashBoardActions.listApiCallNumTopNForManager({ topn: 10, time: date }))
    },
    closeWarning () {
        dispatch({
            type: dashBoardActionType.CLOSE_APPROVAL_WARNING
        })
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class Dashboard extends Component {
    state = {
        dashBoardView: ''
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        let oldData = this.props.dashBoard.alarmTrend;

        let newData = nextProps.dashBoard.alarmTrend;

        if (isEmpty(oldData) && !isEmpty(newData)) {
            this.initLineChart(newData)
        }
    }
    isAdmin () {
        const menuList = this.props.common.menuList || [];
        if (menuList.indexOf('overview_market_menu') > -1) {
            return true
        } else {
            return false
        }
    }
    getNowView () {
        const isAdmin = this.isAdmin();
        // 假如不是管理员，则直接返回Myview视图
        if (!isAdmin) {
            return 'MyView'
        }
        // 假如是管理员，但是没有dashBoardView,则返回overview
        if (!this.state.dashBoardView) {
            return 'overView'
        }
        // 是管理员并且有dashBoardView
        return this.state.dashBoardView;
    }
    getDashBoardView () {
        if (this.getNowView() == 'overView') {
            return <AdminDashboard {...this.props}></AdminDashboard>
        }
        return <UserDashboard {...this.props}></UserDashboard>
    }

    handleClick = (e) => {
        this.setState({
            dashBoardView: e.key
        });
    }
    getMenuView () {
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
                    我的API
                </Menu.Item>
            </Menu>);
    }

    render () {
        return (
            <div className="dashboard nobackground">
                {this.getMenuView()}
                {this.getDashBoardView()}
            </div>
        )
    }
}

export default Dashboard
