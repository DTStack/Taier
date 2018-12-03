import React, { Component } from 'react'
import { connect } from 'react-redux';
import { Tabs, Radio, Modal, message } from 'antd'
import ManageTopCard from './topCard'
import { apiMarketActions } from '../../../actions/apiMarket';
import { apiManageActions } from '../../../actions/apiManage';
import { EXCHANGE_ADMIN_API_STATUS } from '../../../consts'
import ManageErrorLog from './tabPanes/errorLog'
import ApiManageCallState from './tabPanes/callState'
import BuyManageState from './tabPanes/buyState'

const confirm = Modal.confirm;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getApiDetail (apiId) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo (apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId,
                useAdmin: true
            })
        )
    },
    deleteApi (apiId) {
        return dispatch(apiManageActions.deleteApi({ apiIds: [apiId] }));
    },
    openApi (apiId) {
        return dispatch(apiManageActions.openApi(apiId));
    },
    closeApi (apiId) {
        return dispatch(apiManageActions.closeApi(apiId));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIManageDetail extends Component {
    state = {
        apiId: '',
        nowView: 'callState',
        callStateDate: '1',
        errorLogDate: '7'

    }
    // 删除api
    deleteApi () {
        let apiId = this.state.apiId;
        confirm({
            title: '确认删除?',
            content: '确认删除api',
            onOk: () => {
                this.props.deleteApi(apiId)
                    .then(
                        (res) => {
                            if (res) {
                                message.success('删除成功')
                                this.getApiExtInfo();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    openApi () {
        let apiId = this.state.apiId;
        confirm({
            title: '确认开启?',
            content: '确认开启api',
            onOk: () => {
                this.props.openApi(apiId)
                    .then(
                        (res) => {
                            message.success('开启成功')
                            if (res) {
                                this.getApiExtInfo();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    closeApi () {
        let apiId = this.state.apiId;
        confirm({
            title: '确认禁用?',
            content: '确认禁用api',
            onOk: () => {
                this.props.closeApi(apiId)
                    .then(
                        (res) => {
                            message.success('禁用成功')
                            if (res) {
                                this.getApiExtInfo();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    getApiExtInfo () {
        this.props.getApiExtInfo(this.state.apiId)
    }
    componentDidMount () {
        const apiId = this.props.router.params && this.props.router.params.api;
        if (apiId) {
            this.setState({
                apiId: apiId
            }, () => {
                this.getApiExtInfo();
            })
        }
    }
    callback (key) {
        console.log(key)
        this.setState({
            nowView: key
        })
    }
    chooseCallStateDate (e) {
        this.setState({
            callStateDate: e.target.value
        });
    }
    chooseErrorLogDate (e) {
        this.setState({
            errorLogDate: e.target.value
        });
    }
    getValue (key) {
        const api = this.props.apiMarket && this.props.apiMarket.apiCallInfo && this.props.apiMarket.apiCallInfo[this.state.apiId];
        if (api) {
            return api[key]
        } else {
            return null;
        }
    }
    getDateTypeView () {
        switch (this.state.nowView) {
            case 'callState':
                return (
                    <div
                        className="m-radio-group"
                        key="callStateDate"
                        style={{ marginTop: '4px', marginRight: '8px' }}
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
                            <RadioButton value='-1'>历史以来</RadioButton>
                        </RadioGroup>
                    </div>
                );
            case 'errorLog':
                return (
                    <div
                        className="m-radio-group"
                        key="errorLogDate"
                        style={{ marginTop: '4px', marginRight: '8px' }}
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
        let status = EXCHANGE_ADMIN_API_STATUS[this.getValue('status')];
        let isDisAble;
        if (status == 'stop') {
            isDisAble = true;
        }
        console.log(status);

        return (
            <div>
                <ManageTopCard {...this.state} {...this.props}
                    deleteApi={this.deleteApi.bind(this)}
                    openApi={this.openApi.bind(this)}
                    closeApi={this.closeApi.bind(this)}
                ></ManageTopCard>
                <div className="tabs-box m-tabs noheight tabs-filter-show">
                    <Tabs

                        defaultActiveKey={this.state.nowView}
                        onChange={this.callback.bind(this)}
                        tabBarExtraContent={this.getDateTypeView()}
                    >
                        <Tabs.TabPane tab="调用情况" key="callState">
                            <ApiManageCallState dateType={this.state.callStateDate} apiId={this.state.apiId}></ApiManageCallState>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="错误日志" key="errorLog">
                            <ManageErrorLog dateType={this.state.errorLogDate} apiId={this.state.apiId}></ManageErrorLog>
                        </Tabs.TabPane>
                        <Tabs.TabPane tab="订购情况" key="buyState">
                            <BuyManageState statusDisAble={isDisAble} apiId={this.state.apiId}></BuyManageState>
                        </Tabs.TabPane>
                    </Tabs>
                </div>

            </div>
        )
    }
}

export default APIManageDetail;
