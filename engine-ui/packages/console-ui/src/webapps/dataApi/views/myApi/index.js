import React, { Component } from 'react'
import { Menu, Card, Table, Tabs } from "antd"
import { connect } from "react-redux";
import { mineActions } from '../../actions/mine';
import NoApprovedCard from "./NoApprovedCard"
import ApprovedCard from "./ApprovedCard"

const mapStateToProps = state => {
    const { user, mine } = state;
    return { mine, user }
};

const mapDispatchToProps = dispatch => ({
    getApplyingList(currentPage, orderBy, sort) {
        return dispatch(mineActions.getApplyingList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort
        }));
    },
    getAppliedList(currentPage, orderBy, sort, status) {
        return dispatch(mineActions.getAppliedList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            status: status
        }));
    },
    updateApplyStatus(id,status){
        return dispatch(mineActions.updateApplyStatus({
            applyId:id,
            status: status
        }));
    },
    getApiCallInfo(id,time){
        return dispatch(mineActions.getApiCallInfo({
            apiId:id,
            time:time
        }));
    },
   
    getApiCallErrorInfo(id){
        return dispatch(mineActions.getApiCallErrorInfo({
            apiId:id
        }));
    },
    getApiCallUrl(id){
        return dispatch(mineActions.getApiCallUrl({
            apiId:id
        }));
    },
    queryApiCallLog(id,currentPage,bizType){
        return dispatch(mineActions.queryApiCallLog({
            apiId:id,
            currentPage:currentPage,
            bizType:bizType
        }));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class MyAPI extends Component {

    state = {
        nowView: "notApproved",
        pageIndex: 1
    }
    handleClick(e) {

        this.setState({
            nowView: e.key
        })
    }
    componentWillMount() {
        const view=this.props.router.params.view;
        if(view){
            this.setState({
                nowView:view
            })
        }
    }
    getCardView() {
        if (this.state.nowView && this.state.nowView == "notApproved") {
            return <NoApprovedCard {...this.props}></NoApprovedCard>
        }
        return <ApprovedCard {...this.props}></ApprovedCard>
    }
    render() {
        const { children } = this.props
        return (
            <div className=" api-mine nobackground m-card m-tabs">
                <h1 className="box-title">我的API</h1>
                <Card
                style={{marginTop:"0px"}}
                className="box-1"
                noHovering>
                    <Tabs
                        defaultActiveKey={this.state.nowView}
                        onChange={this.handleClick.bind(this)}

                    >
                        <Tabs.TabPane tab="未审批" key="notApproved">
                            <NoApprovedCard {...this.props}></NoApprovedCard>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="已审批" key="approved">
                            <ApprovedCard {...this.props}></ApprovedCard>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>


            </div>
        )
    }
}

export default MyAPI
