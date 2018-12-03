import React, { Component } from 'react';
import { connect } from 'react-redux';

import { API_USER_STATUS } from '../../../consts/index.js';
import Content from '../../../components/apiContent';
import { apiMarketActions } from '../../../actions/apiMarket';

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
        callLimit: 0
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
                            callLimit: res.data.callLimit
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
                this.updateData(nextApiId, nextStatus, mode);
            }
        }
    }
    updateData (apiId, status, mode) {
        this.setState({
            callUrl: '',
            beginTime: undefined,
            endTime: undefined,
            callLimit: 0
        })
        if (!apiId) {
            return;
        }

        if (mode != 'manage') {
            this.getApiCallUrl(apiId);
        }

        this.props.getApiDetail(apiId);
        this.props.getApiExtInfo(apiId);
    }
    render () {
        const { callUrl, beginTime, endTime, callLimit } = this.state;
        const { showRecord = {}, apiMarket, mode, showUserInfo } = this.props;
        let { apiId, id } = showRecord;
        apiId = mode == 'manage' ? id : apiId;

        return (
            <div>
                <div style={{ paddingLeft: 30, marginTop: '20px' }}>
                    <Content showUserInfo={showUserInfo} callLimit={callLimit} beginTime={beginTime} endTime={endTime} showRecord={showRecord} mode={mode} callUrl={callUrl} apiMarket={apiMarket} apiId={apiId} />
                </div>
            </div>
        )
    }
}
export default ApiCallMethod;
