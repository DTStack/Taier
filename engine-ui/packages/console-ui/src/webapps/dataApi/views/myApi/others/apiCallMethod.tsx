import * as React from 'react';
import { connect } from 'react-redux';
import Content from '../../../components/apiContent';
import { apiMarketActions } from '../../../actions/apiMarket';
import apiManage from '../../../api/apiManage'
import { API_TYPE } from '../../../consts';

const mapStateToProps = (state: any) => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    getApiDetail (apiId: any) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo (apiId: any) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class ApiCallMethod extends React.Component<any, any> {
    state: any = {
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

    getApiCallUrl (apiId: any) {
        this.props.getApiCallUrl(apiId)
            .then(
                (res: any) => {
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
    resetToken = (apiId: number) => {
        this.props.resetToken(apiId).then((res: any) => {
            if (res) {
                this.setState({ token: res.data && res.data.token })
            }
        })
    }
    componentDidMount () {
        const { showRecord = {}, mode } = this.props;
        let { apiId, status, id } = showRecord;
        apiId = mode == 'manage' ? id : apiId;

        this.updateData(apiId, status, mode);
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
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
    fetchSecurityList (apiId: any) {
        apiManage.listSecurityGroupByApiId({ apiId }).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    securityList: res.data
                })
            }
        });
    }
    updateData (apiId: any, status: any, mode: any, showRecord?: any) {
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
    getRegisterInfo (apiId: any) {
        apiManage.getRegisterInfo({ apiId }).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    registerInfo: res.data
                })
            }
        })
    }
    fetchApiConfig (apiId: any) {
        apiManage.getApiConfigInfo({ apiId }).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    apiConfig: res.data
                })
            }
        })
    }
    isRegister (showRecord: any = {}) {
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
                        resetToken={this.resetToken}
                        apiMarket={apiMarket}
                        apiVersionCode={apiVersionCode}
                        apiId={apiId} />
                </div>
            </div>
        )
    }
}
export default ApiCallMethod;
