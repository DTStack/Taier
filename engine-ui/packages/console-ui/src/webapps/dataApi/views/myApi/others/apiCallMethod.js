import React, { Component } from 'react';
import { connect } from 'react-redux';
import Content from '../../../components/apiContent';
import { apiMarketActions } from '../../../actions/apiMarket';
import apiManage from '../../../api/apiManage'
import { API_TYPE } from '../../../consts';

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
                apiId: apiId
            })
        )
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class ApiCallMethod extends Component {
    state = {
        callUrl: '',
        beginTime: undefined,
        endTime: undefined,
        apiVersionCode: undefined,
        callLimit: 0,
        token: '',
        apiConfig: {},
        registerInfo: {},
        securityList: []
    }

    getApiCallUrl (apiId) {
        this.props.getApiCallUrl(apiId)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            callUrl: res.data.url,
                            beginTime: res.data.beginTime,
                            endTime: res.data.endTime,
                            callLimit: res.data.callLimit,
                            token: res.data.token,
                            apiVersionCode: res.data.apiVersionCode
                        })
                    }
                }
            );
    }
    componentDidMount () {
        const { showRecord = {}, mode } = this.props;
        let { apiId, status, id } = showRecord;
        apiId = mode == 'manage' ? id : apiId;

        this.updateData(apiId, status, mode);
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { showRecord = {} } = this.props;
        let { apiId, status, id } = showRecord;
        const { showRecord: nextShowRecord = {}, slidePaneShow, mode } = nextProps;
        let { apiId: nextApiId, status: nextStatus, id: nextId } = nextShowRecord;

        apiId = mode == 'manage' ? id : apiId;
        nextApiId = mode == 'manage' ? nextId : nextApiId;

        if (apiId != nextApiId || nextStatus != status) {
            if (slidePaneShow) {
                this.updateData(nextApiId, nextStatus, mode, nextShowRecord);
            }
        }
    }
    fetchSecurityList (apiId) {
        apiManage.listSecurityGroupByApiId({ apiId }).then((res) => {
            if (res.code == 1) {
                this.setState({
                    securityList: res.data
                })
            }
        });
    }
    updateData (apiId, status, mode, showRecord) {
        this.setState({
            callUrl: '',
            beginTime: undefined,
            endTime: undefined,
            callLimit: 0,
            securityList: [],
            token: '',
            apiConfig: {}
        })
        if (!apiId) {
            return;
        }

        if (mode != 'manage') {
            this.getApiCallUrl(apiId);
        } else if (this.isRegister(showRecord)) {
            /**
             * 获取注册api配置信息
             */
            this.getRegisterInfo(apiId)
        } else {
            /**
             * 获取数据源信息
             */
            this.fetchApiConfig(apiId);
        }

        this.props.getApiDetail(apiId);
        this.props.getApiExtInfo(apiId);
    }
    getRegisterInfo (apiId) {
        apiManage.getRegisterInfo({ apiId }).then((res) => {
            if (res.code == 1) {
                this.setState({
                    registerInfo: res.data
                })
            }
        })
    }
    fetchApiConfig (apiId) {
        apiManage.getApiConfigInfo({ apiId }).then((res) => {
            if (res.code == 1) {
                this.setState({
                    apiConfig: res.data
                })
            }
        })
    }
    isRegister (showRecord = {}) {
        let { apiType } = showRecord;
        return apiType == API_TYPE.REGISTER
    }
    render () {
        const {
            callUrl,
            beginTime,
            endTime,
            callLimit,
            apiConfig,
            registerInfo,
            token,
            apiVersionCode
        } = this.state;
        const { showRecord = {}, apiMarket, mode, showUserInfo } = this.props;
        let { apiId, id, apiType } = showRecord;
        apiId = mode == 'manage' ? id : apiId;

        return (
            <div>
                <div style={{ paddingLeft: 30, marginTop: '20px' }}>
                    <Content
                        showSecurity={mode != 'manage'}
                        showApiConfig={mode == 'manage'}
                        apiConfig={apiConfig}
                        registerInfo={registerInfo}
                        showUserInfo={showUserInfo}
                        callLimit={callLimit}
                        beginTime={beginTime}
                        endTime={endTime}
                        isRegister={apiType == API_TYPE.REGISTER}
                        showRecord={showRecord}
                        mode={mode}
                        callUrl={callUrl}
                        token={token}
                        apiMarket={apiMarket}
                        apiVersionCode={apiVersionCode}
                        apiId={apiId} />
                </div>
            </div>
        )
    }
}
export default ApiCallMethod;
