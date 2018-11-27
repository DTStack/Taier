import React, { Component } from 'react'
import { Alert, Card, Col, Row, Table, Radio, Tabs } from 'antd';
import { Link } from 'react-router';
import TopCall from './topCall';
import TopFail from './topFail';
import TopCallFunc from './topCallFunc';
import ErrorDistributed from './errorDistributed';

import OverView from './overView'

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

class AdminDashboard extends Component {
    state = {
        nowView: 'callTop'
    }

    componentDidMount () {
        this.props.chooseAdminDate(10, {
            target: {
                value: this.props.dashBoard.adminDate
            }
        })
    }
    topViewChange (key) {
        this.setState({
            nowView: key
        })
    }
    getDateChoose () {
        return this.props.dashBoard.adminDate || '1';
    }

    render () {
        const { approvalWarning } = this.props.dashBoard;
        const approval_alert = (this.props.dashBoard.approvedMsgCount > 0 && approvalWarning) ? (
            <Alert
                message={<span>您有{this.props.dashBoard.approvedMsgCount}条未处理的Api申请，请您及时处理。<Link to="/api/approval?status=0" >立即审批</Link> </span>}
                type="warning"
                closable
                onClose={this.props.closeWarning}
            />
        ) : null
        const marketOverview = this.props.dashBoard.marketOverview[this.getDateChoose()];
        return (
            <div>
                <div className="padding_t14_lr20">
                    {approval_alert}
                </div>
                <div className="box-title m-radio-group">
                    <span>调用情况</span>
                    <RadioGroup
                        defaultValue={this.getDateChoose()}
                        className="no-bd nobackground"
                        onChange={this.props.chooseAdminDate.bind(this, 10)}
                        style={{ marginTop: '8.5px', float: 'right' }}
                    >
                        <RadioButton value='1'>最近24小时</RadioButton>
                        <RadioButton value='7'>最近7天</RadioButton>
                        <RadioButton value='30'>最近30天</RadioButton>
                    </RadioGroup>
                </div>
                <div className="box-card m-card">
                    <Row gutter={20}>
                        <Col span={16}>
                            <OverView date={this.props.dashBoard.adminDate} chartData={marketOverview.callInfo.infoList} callCount={marketOverview.callInfo.callCount} apiNum={marketOverview.callInfo.apiNum} failPercent={marketOverview.callInfo.failPercent} ></OverView>
                        </Col>
                        <Col span={8} className="m-card-small m-tabs noheight">

                            <Tabs
                                defaultActiveKey={this.state.nowView}
                                onChange={this.topViewChange.bind(this)}
                                className="shadow"

                            >
                                <Tabs.TabPane tab="调用用户Top10" key="callTop">
                                    <TopCall data={marketOverview.callCountTop}></TopCall>
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="调用量Top10" key="callFuncTop">
                                    <TopCallFunc idAdmin={true} router={this.props.router} data={marketOverview.topCallFunc}></TopCallFunc>
                                </Tabs.TabPane>
                            </Tabs>

                        </Col>
                    </Row>
                    <Row className="m-card-small margin-t20" gutter={20}>
                        <Col span={16}>
                            <ErrorDistributed chartData={marketOverview.failInfoList}></ErrorDistributed>
                        </Col>
                        <Col span={8}>
                            <TopFail isAdmin={true} router={this.props.router} data={marketOverview.callFailTop}></TopFail>
                        </Col>
                    </Row>

                </div>

            </div>
        )
    }
}

export default AdminDashboard;
