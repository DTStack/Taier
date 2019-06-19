import React, { Component } from 'react'
import { Card, Tabs } from 'antd'
import { connect } from 'react-redux';

import { mineActions } from '../../actions/mine';
import NoApprovedCard from './noApprovedCard'
import ApprovedCard from './approvedCard'

const mapStateToProps = state => {
    const { user, mine } = state;
    return { mine, user }
};

const mapDispatchToProps = dispatch => ({
    getApplyingList (currentPage, orderBy, sort, apiName) {
        return dispatch(mineActions.getApplyingList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            apiName: apiName
        }));
    },
    getAppliedList (currentPage, orderBy, sort, status, apiName) {
        return dispatch(mineActions.getAppliedList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            status: status,
            apiName: apiName
        }));
    },
    updateApplyStatus (id, status) {
        return dispatch(mineActions.updateApplyStatus({
            applyId: id,
            status: status
        }));
    },
    getApiCallInfo (id, time) {
        return dispatch(mineActions.getApiCallInfo({
            apiId: id,
            time: time
        }));
    },

    getApiCallErrorInfo (id, time) {
        return dispatch(mineActions.getApiCallErrorInfo({
            apiId: id,
            time: time
        }));
    },
    getApiCallUrl (id) {
        return dispatch(mineActions.getApiCallUrl({
            apiId: id
        }));
    },
    queryApiCallLog (id, currentPage, bizType, time) {
        return dispatch(mineActions.queryApiCallLog({
            apiId: id,
            currentPage: currentPage,
            bizType: bizType,
            pageSize: 5,
            time: time
        }));
    },
    getApiCreatorInfo (apiId) {
        return dispatch(mineActions.getApiCreatorInfo({
            apiId: apiId
        }));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class MyAPI extends Component {
    state = {
        nowView: 'approved',
        pageIndex: 1
    }
    handleClick (e) {
        this.setState({
            nowView: e
        })

        if (e == 'approved') {
            this.props.router.replace('/api/mine/myApi/approved')
        } else {
            this.props.router.replace('/api/mine/myApi/notApproved')
        }
    }
    // eslint-disable-next-line
	componentWillMount () {
        const view = this.props.router.params.view;

        if (view) {
            this.setState({
                nowView: view
            })
        }
    }
    getCardView () {
        if (this.state.nowView && this.state.nowView == 'notApproved') {
            return <NoApprovedCard {...this.props}></NoApprovedCard>
        }
        return <ApprovedCard {...this.props}></ApprovedCard>
    }
    render () {
        const { nowView } = this.state;
        return (
            <div className="api-mine nobackground m-card m-tabs">
                <p className="o-box__title">我的API</p>
                <Card
                    className="no-card-border"
                    style={{ minHeight: 'calc(100% - 65px)' }}
                    noHovering>
                    <Tabs
                        style={{ overflow: 'unset', minHeight: 'calc(100% - 67px)' }}
                        animated={false}
                        defaultActiveKey={nowView}
                        onChange={this.handleClick.bind(this)}
                    >
                        <Tabs.TabPane tab="已审批" key="approved">
                            <ApprovedCard nowView={nowView} apiId={this.props.location.query && this.props.location.query.apiId} {...this.props}></ApprovedCard>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="审批中" key="notApproved">
                            <NoApprovedCard nowView={nowView} apiId={this.props.location.query && this.props.location.query.apiId} {...this.props}></NoApprovedCard>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>
            </div>
        )
    }
}

export default MyAPI
