import React, { Component } from 'react'
import { Alert, Card, Col, Row, Table, Radio,Tabs } from 'antd';


import TopCallFunc from "./TopCallFunc";
import HaveNoApi from "./HaveNoApi"
import OverView from "./overView"
import TopFail from "./TopFail"



const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

class UserDashboard extends Component {
    state={
        nowView:"callTop"
    }
    componentDidMount() {
        this.props.chooseUserDate(10, {
            target: {
                value: this.props.dashBoard.userDate
            }
        })
    }
    topViewChange(key){
        this.setState({
            nowView: key
        })
    }
    getDateChoose() {
        return this.props.dashBoard.userDate || '1';
    }

    isApiExist() {
        return true;
    }


    getView = () => {
        if (this.isApiExist()) {
            return this.getMainView();
        }
        return <HaveNoApi></HaveNoApi>
    }


    getMainView = () => {
        const userOverview = this.props.dashBoard.userOverview[this.getDateChoose()];
        return (
            <div>
                <div className="box-title m-radio-group">
                    <span>调用情况</span>
                    <RadioGroup
                        defaultValue={this.getDateChoose()}
                        className="no-bd nobackground"
                        onChange={this.props.chooseUserDate.bind(this, 10)}
                        style={{ marginTop: '8.5px', float: "right" }}
                    >
                        <RadioButton value='1'>最近24小时</RadioButton>
                        <RadioButton value='7'>最近7天</RadioButton>
                        <RadioButton value='30'>最近30天</RadioButton>
                    </RadioGroup>
                </div>
                <div className="box-card m-card">
                    <Row gutter={20}>
                        <Col span={16}>
                            <OverView chartData={userOverview.callInfo.infoList} callCount={userOverview.callInfo.callCount} topCallFunc={userOverview.callInfo.callTopAPI} failPercent={userOverview.callInfo.failPercent} ></OverView>
                        </Col>
                        <Col span={8} className="m-card-small  m-tabs noheight">
                            <Tabs
                                style={{borderTop:"1px #dcdcdc solid"}}
                                defaultActiveKey={this.state.nowView}
                                onChange={this.topViewChange.bind(this)}
                                
                            >
                                <Tabs.TabPane tab="调用量Top10" key="callTop">
                                    <TopCallFunc data={userOverview.callCountTop}></TopCallFunc>
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="错误率Top10" key="failTop">
                                    <TopFail cardHeight="363" noTitle data={userOverview.callFailTop}></TopFail>
                                </Tabs.TabPane>
                            </Tabs>
                            
                        </Col>
                    </Row>
                    <Row className="m-card-small margin-t20" gutter={20}>
                        <Col span={16}>
                            <Card
                                noHovering
                                title="订购情况"
                            >
                                <Row gutter={130} className="m-count padding-l20 height-101" justify="space-around" type="flex">
                                    <Col span={6} >
                                        <section className="m-count-section margin-t20" style={{ width: 100 }}>
                                            <span className="m-count-title text-left">审批中</span>
                                            <span className="m-count-content font-organge text-left">{userOverview.approvalInfo.approvingNum}</span>
                                        </section>
                                    </Col>
                                    <Col span={6}>
                                        <section className="m-count-section margin-t20" style={{ width: 100 }}>
                                            <span className="m-count-title text-left">审批通过</span>
                                            <span className="m-count-content font-green text-left">{userOverview.approvalInfo.approvedNum}</span>
                                        </section>
                                    </Col>
                                    <Col span={6}>
                                        <section className="m-count-section margin-t20" style={{ width: 100 }}>
                                            <span className="m-count-title text-left">已停用接口</span>
                                            <span className="m-count-content font-gray text-left">{userOverview.approvalInfo.stoppedNum}</span>
                                        </section>
                                    </Col>
                                </Row>
                            </Card>
                        </Col>
                    </Row>


                </div>
            </div>
        )
    }

    render() {
        return (
            <div>
                {this.getView()}
            </div>
        )
    }
}

export default UserDashboard;