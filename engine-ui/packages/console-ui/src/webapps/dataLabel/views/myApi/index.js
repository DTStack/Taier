import React, { Component } from 'react'
import { Menu, Card, Table, Tabs } from "antd"
import { connect } from "react-redux";
import { mineActions } from '../../actions/mine';
import NoApprovedCard from "./noApprovedCard"
import ApprovedCard from "./approvedCard"

const mapStateToProps = state => {
    const { user, mine } = state;
    return { mine, user }
};

const mapDispatchToProps = dispatch => ({
    getApplyingList(currentPage, orderBy, sort,apiName) {
        return dispatch(mineActions.getApplyingList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            apiName:apiName
        }));
    },
    getAppliedList(currentPage, orderBy, sort, status,apiName) {
        return dispatch(mineActions.getAppliedList({
            currentPage: currentPage,
            pageSize: 20,
            orderBy: orderBy,
            sort: sort,
            status: status,
            apiName:apiName
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
            tagId:id,
            time:time
        }));
    },
   
    getApiCallErrorInfo(id){
        return dispatch(mineActions.getApiCallErrorInfo({
            tagId:id
        }));
    },
    getApiCallUrl(id){
        return dispatch(mineActions.getApiCallUrl({
            tagId:id
        }));
    },
    queryApiCallLog(id,currentPage,bizType){
        return dispatch(mineActions.queryApiCallLog({
            tagId:id,
            currentPage:currentPage,
            bizType:bizType,
            pageSize:5
        }));
    },
    getApiCreatorInfo(tagId){
        return dispatch(mineActions.getApiCreatorInfo({
            tagId:tagId
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
            nowView: e
        })
      
        if(e=="approved"){
            this.props.router.replace("/dl/mine/approved")
        }else{
            this.props.router.replace("/dl/mine")
        }
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
        const { children } = this.props;
        return (
            <div className="api-mine nobackground m-card height-auto m-tabs"> 
                <Card
                className="box-1"
                bordered={false}
                noHovering>
                    <Tabs
                        defaultActiveKey={this.state.nowView}
                        onChange={this.handleClick.bind(this)}
                    >
                        <Tabs.TabPane tab="未审批" key="notApproved">
                            <NoApprovedCard tagId={this.props.location.query&&this.props.location.query.tagId} {...this.props}></NoApprovedCard>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="已审批" key="approved">
                            <ApprovedCard tagId={this.props.location.query&&this.props.location.query.tagId} {...this.props}></ApprovedCard>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>


            </div>
        )
    }
}

export default MyAPI
