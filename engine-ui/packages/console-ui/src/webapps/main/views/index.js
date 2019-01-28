import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { browserHistory } from 'react-router'
import NotFund from 'widgets/notFund'
import { getLicenseApp } from '../actions/app'
import GlobalLoading from './layout/loading'
import { getInitUser } from '../actions/user'
import userActions from '../consts/userActions'
import { initNotification } from 'funcs';
import http from '../api';

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

initNotification();
@connect(state => {
    return {
        user: state.user,
        licenseApps: state.licenseApps,
        routing: state.routing
    }
})

class Main extends Component {
    componentDidMount () {
        const { user } = this.props;
        const userAction = getInitUser()
        this.props.dispatch(userAction);
        this.checkRoot(user);
        this.props.dispatch(getLicenseApp());
        this.isEnableLicenseApp();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { user } = nextProps;

        if (this.props.user.dtuicUserId != user.dtuicUserId && user.dtuicUserId) {
            this.checkRoot(user);
        }
        if (this.props.routing) {
            if (this.props.routing.locationBeforeTransitions.pathname != nextProps.routing.locationBeforeTransitions.pathname) {
                this.isEnableLicenseApp();
            }
        }
    }
    componentDidUpdate (prevProps, prevState) {
        if (prevProps.licenseApps != this.props.licenseApps) {
            this.isEnableLicenseApp();
        }
    }
    getCurrentPath () {
        return browserHistory.getCurrentLocation().pathname + browserHistory.getCurrentLocation().hash
    }
    // license禁用app url 跳转到首页
    isEnableLicenseApp () {
        const { licenseApps } = this.props;
        // 成功返回数据
        if (licenseApps && licenseApps.length > 1) {
            // rdosAPP
            const rdosApp = licenseApps[0];
            const isRdosShow = rdosApp.isShow;
            const isRdosTask = rdosApp.children[0].isShow;
            const isRdosOpera = rdosApp.children[1].isShow;
            const isRdosDataSource = rdosApp.children[2].isShow;
            const isRdosPro = rdosApp.children[3].isShow;
            const isRdosMap = rdosApp.children[4].isShow;
            const isRdosModal = rdosApp.children[5].isShow;
            // streamAPP
            const streamApp = licenseApps[1];
            const isStream = streamApp.isShow;
            const isStreamDataSource = streamApp.children[0].isShow;
            const isStreamTask = streamApp.children[1].isShow;
            const isStreamPro = streamApp.children[2].isShow;
            const isStreamOpera = streamApp.children[3].isShow;
            // analyticsEngine
            const analyApp = licenseApps[2];
            const isAna = analyApp.isShow;
            // dataQuality
            const qualityApp = licenseApps[3];
            const isQuali = qualityApp.isShow;
            const isQualiOver = qualityApp.children[0].isShow;
            const isQualiTaskSearch = qualityApp.children[1].isShow;
            const isQualiRule = qualityApp.children[2].isShow;
            const isQualiVali = qualityApp.children[3].isShow;
            const isQualiDataSource = qualityApp.children[4].isShow;
            // dataApi
            const apiApp = licenseApps[4];
            const isDataApi = apiApp.isShow;
            const isApiover = apiApp.children[0].isShow;
            const isApiMarket = apiApp.children[1].isShow;
            const isApiMine = apiApp.children[2].isShow;
            const isApiMana = apiApp.children[3].isShow;
            const isApiSafe = apiApp.children[4].isShow;
            const isApiDataSource = apiApp.children[5].isShow;
            // rdos url
            if (this.getCurrentPath().indexOf('batch.html') > -1 && !isRdosShow) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/offline/task') > -1 && !isRdosTask) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/operation') > -1 && !isRdosOpera) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/database') > -1 && !isRdosDataSource) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/project') > -1 && !isRdosPro) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/data-manage') > -1 && !isRdosMap) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html#/data-model') > -1 && !isRdosModal) {
                window.location.href = '/'
            }
            // stream url
            if (this.getCurrentPath().indexOf('stream.html') > -1 && !isStream) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('stream.html#/database') > -1 && !isStreamDataSource) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('stream.html#/realtime') > -1 && !isStreamTask) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('stream.html#/project') > -1 && !isStreamPro) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('stream.html#/operation') > -1 && !isStreamOpera) {
                window.location.href = '/'
            }
            // analyticsEngine url
            if (this.getCurrentPath().indexOf('analytics.html') > -1 && !isAna) {
                window.location.href = '/'
            }
            // dataQuality url
            if (this.getCurrentPath().indexOf('dataQuality.html') > -1 && !isQuali) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html#/dq/overview') > -1 && !isQualiOver) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html#/dq/taskQuery') > -1 && !isQualiTaskSearch) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html#/dq/rule') > -1 && !isQualiRule) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html#/dq/dataCheck') > -1 && !isQualiVali) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html#/dq/dataSource') > -1 && !isQualiDataSource) {
                window.location.href = '/'
            }
            // dataApi url
            if (this.getCurrentPath().indexOf('dataApi.html') > -1 && !isDataApi) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api') > -1 && !isApiover) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api/market') > -1 && !isApiMarket) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api/mine') > -1 && !isApiMine) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api/manage') > -1 && !isApiMana) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api/manage') > -1 && !isApiSafe) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html#/api/dataSource') > -1 && !isApiDataSource) {
                window.location.href = '/'
            }
        }
        // 用户未上传license
        if (licenseApps && licenseApps.length == 0) {
            if (this.getCurrentPath().indexOf('batch.html') > -1) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('batch.html') > -1) {
                window.location.href = '/stream.html'
            }
            if (this.getCurrentPath().indexOf('analytics.html') > -1) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataQuality.html') > -1) {
                window.location.href = '/'
            }
            if (this.getCurrentPath().indexOf('dataApi.html') > -1) {
                window.location.href = '/'
            }
        }
        console.log('enter')
    }
    checkRoot (user) {
        if (user && user.dtuicUserId) {
            http.checkRoot({ userId: user.dtuicUserId })
                .then(
                    (res) => {
                        if (res.code == 1) {
                            this.props.dispatch({
                                type: userActions.UPDATE_USER,
                                data: {
                                    isRoot: true
                                }
                            })
                        } else {
                            this.props.dispatch({
                                type: userActions.UPDATE_USER,
                                data: {
                                    isRoot: false
                                }
                            })
                        }
                    }
                )
                .catch(
                    (e) => {
                        console.log('控制台权限')
                    }
                );
        }
    }

    render () {
        let { children } = this.props;
        if (this.props.licenseApps && this.props.licenseApps.length != 0 && this.props.licenseApps.length <= 1) {
            children = <GlobalLoading />
        }
        return children || <NotFund />
    }
}

Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
