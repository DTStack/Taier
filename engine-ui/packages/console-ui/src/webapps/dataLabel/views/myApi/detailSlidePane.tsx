import * as React from 'react';
import { Tabs, Radio } from 'antd'
import SlidePane from 'widgets/slidePane';
import ApiCallMethod from './others/apiCallMethod';
import ApiCallState from './others/apiCallState';
import ErrorLog from './others/errorLog';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
class DetailSlidePane extends React.Component<any, any> {
    state: any = {
        approvedText: '同意',
        applyText: '申请调用此接口，请批准',
        nowView: 'callState',
        callStateDate: '1',
        errorLogDate: '7'

    }

    callback (key: any) {
        console.log(key)
        this.setState({
            nowView: key
        })
    }
    chooseCallStateDate (e: any) {
        this.setState({
            callStateDate: e.target.value
        });
    }
    chooseErrorLogDate (e: any) {
        this.setState({
            errorLogDate: e.target.value
        });
    }

    getDateTypeView () {
        switch (this.state.nowView) {
            case 'callState':
                return (
                    <div
                        className="m-radio-group"
                        key="callStateDate"
                        style={{ marginTop: 4, marginRight: 28 }}
                    >
                        <RadioGroup

                            name="callStateDate"
                            defaultValue={this.state.callStateDate}
                            className="no-bd nobackground"
                            onChange={this.chooseCallStateDate.bind(this)}
                        >
                            <RadioButton value='1'>最近24小时</RadioButton>
                            <RadioButton value='7'>最近7天</RadioButton>
                            <RadioButton value='30'>最近30天</RadioButton>
                        </RadioGroup>
                    </div>
                );
            case 'errorLog':
                return (
                    <div
                        className="m-radio-group"
                        key="errorLogDate"
                        style={{ marginTop: 4, marginRight: 28 }}
                    >
                        <RadioGroup

                            name="errorLogDate"
                            defaultValue={this.state.errorLogDate}
                            className="no-bd nobackground"
                            onChange={this.chooseErrorLogDate.bind(this)}
                        >
                            <RadioButton value='7'>最近7天</RadioButton>
                        </RadioGroup>
                    </div>
                );
            default:
                return null;
        }
    }

    render () {
        const callMethodView = this.props.showRecord.status == 3 || this.props.showRecord.apiStatus == 1 ? null : (
            <Tabs.TabPane tab="调用方式" key="callMethod">
                <ApiCallMethod {...this.props} ></ApiCallMethod>
            </Tabs.TabPane>
        )
        return (

            <SlidePane
                className="m-tabs tabs-filter-show"
                visible={this.props.slidePaneShow}
                style={{ right: '-20px', width: '80%', minHeight: '350px' }}
                onClose={this.props.closeSlidePane}>
                <Tabs
                    activeKey={this.state.nowView}
                    onChange={this.callback.bind(this)}
                    tabBarExtraContent={this.getDateTypeView()}
                >
                    <Tabs.TabPane tab="调用情况" key="callState">
                        <ApiCallState {...this.props} dateType={this.state.callStateDate}></ApiCallState>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab="错误日志" key="errorLog">
                        <ErrorLog {...this.props} dateType={this.state.errorLogDate}></ErrorLog>
                    </Tabs.TabPane>
                    {callMethodView}
                </Tabs>

            </SlidePane>

        )
    }
}
export default DetailSlidePane;
