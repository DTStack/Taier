import React, { Component } from "react";
import { Tabs, Radio, Row, Col } from "antd"

import SlidePane from "widgets/slidePane";

import { API_SYSTEM_STATUS } from "../../../consts";
import ApiCallMethod from "../../myApi/others/apiCallMethod";
import BuyManageState from "./tabPanes/buyState";
import ApiCallState from "./tabPanes/callState";
import ErrorLog from "./tabPanes/errorLog";


const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
class apiSlidePane extends Component {
    state = {
        approvedText: "同意",
        applyText: "申请调用此接口，请批准",
        nowView: "callMethod",
        date: "1"

    }

    callback(key) {
        console.log(key)
        this.setState({
            nowView: key
        })
    }
    chooseCallStateDate(e) {
        this.setState({
            date: e.target.value
        });
    }
    getDateTypeView() {
        switch (this.state.nowView) {
            case "callState":
                return (
                    <div
                        className="m-radio-group"
                        key="date"
                        style={{ marginTop: 4, marginRight: 28 }}
                    >
                        <RadioGroup

                            name="date"
                            defaultValue={this.state.date}
                            className="no-bd nobackground"
                            onChange={this.chooseCallStateDate.bind(this)}
                        >
                            <RadioButton value='1'>最近24小时</RadioButton>
                            <RadioButton value='7'>最近7天</RadioButton>
                            <RadioButton value='30'>最近30天</RadioButton>
                            <RadioButton value='-1'>历史以来</RadioButton>
                        </RadioGroup>
                    </div>
                );
            default:
                return null;

        }

    }


    render() {
        const { showRecord = {} } = this.props;
        const { status } = showRecord;
        const isDisAble = status == API_SYSTEM_STATUS.STOP ? true : false;

        return (
            <SlidePane
                className="m-tabs tabs-filter-show"
                visible={this.props.slidePaneShow}
                style={{ right: '-20px', width: '80%', minHeight: '800px', height: '100%' }}
                onClose={this.props.closeSlidePane}>
                <Tabs
                    animated={false}
                    activeKey={this.state.nowView}
                    onChange={this.callback.bind(this)}
                    tabBarExtraContent={this.getDateTypeView()}
                >
                    <Tabs.TabPane tab="API详情" key="callMethod">
                        <ApiCallMethod  mode="manage" {...this.props} ></ApiCallMethod>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab="调用情况" key="callState">
                        <h1 className="title-border-l-blue slide-title">调用统计</h1>
                        <ApiCallState apiId={showRecord.id} dateType={this.state.date}></ApiCallState>
                        <h1 className="title-border-l-blue slide-title">错误日志</h1>
                        <div style={{ paddingLeft: "20px", paddingRight: "20px" }}>
                            <Row>
                                <ErrorLog apiId={showRecord.id} dateType={this.state.date}></ErrorLog>
                            </Row>
                        </div>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab="订购情况" key="buyState">
                        <div style={{padding:"10px 15px"}}>
                            <BuyManageState statusDisAble={isDisAble} apiId={showRecord.id}></BuyManageState>
                        </div>
                    </Tabs.TabPane>
                </Tabs>
            </SlidePane>
        )
    }
}
export default apiSlidePane;
