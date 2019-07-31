import * as React from 'react';
import { Tabs, Radio } from 'antd'

import SlidePane from 'widgets/slidePane';

import ApiCallMethod from './others/apiCallMethod';
import ApiCallState from './others/apiCallState';
import ErrorLog from './others/errorLog';
import Security from '../../components/security';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
class detailSlidePane extends React.Component<any, any> {
    state: any = {
        approvedText: '同意',
        applyText: '申请调用此接口，请批准',
        nowView: 'callMethod',
        date: '1'

    }

    callback (key: any) {
        console.log(key)
        this.setState({
            nowView: key
        })
    }
    chooseCallStateDate (e: any) {
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
        const { showRecord } = this.props;
        return (
            <SlidePane
                className="m-tabs"
                visible={this.props.slidePaneShow}
                style={{ right: '0px', width: '80%', minHeight: '600px', height: '100%' }}
                onClose={this.props.closeSlidePane}>
                <Tabs
                    className="l-dt__tabs--scroll"
                    animated={false}
                    activeKey={this.state.nowView}
                    onChange={this.callback.bind(this)}
                    tabBarExtraContent={this.getDateTypeView()}
                >
                    <Tabs.TabPane tab="API详情" key="callMethod">
                        <ApiCallMethod {...this.props} showUserInfo={true} ></ApiCallMethod>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab="安全与限制" key="security">
                        <Security apiId={showRecord.apiId} key={showRecord.apiId} disableEdit={true} />
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
