import React, { Component } from 'react'
import { Menu, Card, Table, Tabs } from 'antd'
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

    getApiCallErrorInfo (id) {
        return dispatch(mineActions.getApiCallErrorInfo({
            apiId: id
        }));
    },
    getApiCallUrl (id) {
        return dispatch(mineActions.getApiCallUrl({
            apiId: id
        }));
    },
    queryApiCallLog (id, currentPage, bizType) {
        return dispatch(mineActions.queryApiCallLog({
            apiId: id,
            currentPage: currentPage,
            bizType: bizType,
            pageSize: 5
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
        nowView: 'notApproved',
        pageIndex: 1
    }
    handleClick (e) {
        this.setState({
            nowView: e
        })

        if (e == 'approved') {
            this.props.router.replace('/dl/mine/approved')
        } else {
            this.props.router.replace('/dl/mine')
        }
    }
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
        const { children } = this.props;
        return (
            <div style={{ height: '100%', overflowX: 'hidden' }}>
                <div className="box-1 m-card shadow m-tabs">
                    <Tabs
                        animated={false}
                        defaultActiveKey={this.state.nowView}
                        onChange={this.handleClick.bind(this)}
                        style={{ height: 'auto', overflow: 'inherit' }}
                    >
                        <Tabs.TabPane tab="未审批" key="notApproved">
                            <NoApprovedCard apiId={this.props.location.query && this.props.location.query.apiId} {...this.props}></NoApprovedCard>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="已审批" key="approved">
                            <ApprovedCard apiId={this.props.location.query && this.props.location.query.apiId} {...this.props}></ApprovedCard>
                        </Tabs.TabPane>
                    </Tabs>
                </div>
            </div>
        )
    }
}

export default MyAPI
