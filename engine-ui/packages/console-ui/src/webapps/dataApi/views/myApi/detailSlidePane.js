import React, { Component } from 'react';
import { Menu, Card, Table, Tabs, Radio } from 'antd'

import SlidePane from 'widgets/slidePane';
import { API_USER_STATUS } from '../../consts/index.js';

import ApiCallMethod from './others/apiCallMethod';
import ApiCallState from './others/apiCallState';
import ErrorLog from './others/errorLog';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
class detailSlidePane extends Component {
    state = {
        approvedText: '同意',
        applyText: '申请调用此接口，请批准',
        nowView: 'callMethod',
        date: '1'

    }

    callback (key) {
        console.log(key)
        this.setState({
            nowView: key
        })
    }
    chooseCallStateDate (e) {
        this.setState({
            date: e.target.value
        });
    }
    getDateTypeView () {
        switch (this.state.nowView) {
        case 'callState':
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
                    </RadioGroup>
                </div>
            );
        default:
            return null;
        }
    }

    render () {
        return (
            <SlidePane
                className="m-tabs tabs-filter-show"
                visible={this.props.slidePaneShow}
                style={{ right: '-20px', width: '80%', minHeight: '750px', height: '100%' }}
                onClose={this.props.closeSlidePane}>
                <Tabs
                    animated={false}
                    activeKey={this.state.nowView}
                    onChange={this.callback.bind(this)}
                    tabBarExtraContent={this.getDateTypeView()}
                >
                    <Tabs.TabPane tab="API详情" key="callMethod">
                        <ApiCallMethod {...this.props} showUserInfo={true} ></ApiCallMethod>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab="调用情况" key="callState">
                        <h1 className="title-border-l-blue slide-title">调用统计</h1>
                        <ApiCallState {...this.props} dateType={this.state.date}></ApiCallState>
                        <h1 className="title-border-l-blue slide-title">错误日志</h1>
                        <ErrorLog {...this.props} dateType={this.state.date}></ErrorLog>
                    </Tabs.TabPane>
                </Tabs>
            </SlidePane>
        )
    }
}
export default detailSlidePane;
