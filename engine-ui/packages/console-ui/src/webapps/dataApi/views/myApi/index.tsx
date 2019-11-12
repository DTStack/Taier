import * as React from 'react'
import { Card, Tabs } from 'antd'
import { connect } from 'react-redux';

import { mineActions } from '../../actions/mine';
import NoApprovedCard from './noApprovedCard'
import ApprovedCard from './approvedCard'

const mapStateToProps = (state: any) => {
    const { user, mine, project } = state;
    return { mine, user, project }
};

const mapDispatchToProps = (dispatch: any) => ({
    getApplyingList (currentPage: any, orderBy: any, sort: any, apiName: any) {
        return dispatch(mineActions.getApplyingList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            apiName: apiName
        }));
    },
    getAppliedList (currentPage: any, orderBy: any, sort: any, status: any, apiName: any) {
        return dispatch(mineActions.getAppliedList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            status: status,
            apiName: apiName
        }));
    },
    updateApplyStatus (id: any, status: any) {
        return dispatch(mineActions.updateApplyStatus({
            applyId: id,
            status: status
        }));
    },
    getApiCallInfo (id: any, time: any) {
        return dispatch(mineActions.getApiCallInfo({
            apiId: id,
            time: time
        }));
    },

    getApiCallErrorInfo (id: any, time: any) {
        return dispatch(mineActions.getApiCallErrorInfo({
            apiId: id,
            time: time
        }));
    },
    getApiCallUrl (id: any) {
        return dispatch(mineActions.getApiCallUrl({
            apiId: id
        }));
    },
    queryApiCallLog (id: any, currentPage: any, bizType: any, time: any) {
        return dispatch(mineActions.queryApiCallLog({
            apiId: id,
            currentPage: currentPage,
            bizType: bizType,
            pageSize: 5,
            time: time
        }));
    },
    getApiCreatorInfo (apiId: any) {
        return dispatch(mineActions.getApiCreatorInfo({
            apiId: apiId
        }));
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class MyAPI extends React.Component<any, any> {
    state: any = {
        nowView: 'approved',
        pageIndex: 1
    }
    handleClick (e: any) {
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
                    style={{ height: 'calc(100% - 45px)' }}
                    bodyStyle={{ height: '100%' }}
                    noHovering>
                    <Tabs
                        style={{ overflow: 'unset', height: '100%' }}
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
