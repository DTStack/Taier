import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
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
        // this.isEnableLicenseApp();
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
        if (this.props.licenseApps.length > 0 && prevProps.licenseApps !== this.props.licenseApps) {
            console.log('componentDidUpdate:', this.props.licenseApps, prevProps.licenseApps)
            this.isEnableLicenseApp();
        }
    }
    getCurrentPath () {
        return document.location.pathname + document.location.hash;
    }
    loopIsIntercept (pathAddress, arr) {
        arr.map(item => {
            if (pathAddress.indexOf(item.url) > -1 && item.isShow) {
                window.location.href = '/'
            }
        })
    }
    // license禁用app url 跳转到首页
    isEnableLicenseApp () {
        const { licenseApps } = this.props;
        const pathAddress = this.getCurrentPath();
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
            // 判断条件存入数组
            const arr = [
                // rdos
                {
                    url: 'batch.html',
                    isShow: !isRdosShow
                },
                {
                    url: 'batch.html#/offline/task',
                    isShow: !isRdosTask
                },
                {
                    url: 'batch.html#/operation',
                    isShow: !isRdosOpera
                },
                {
                    url: 'batch.html#/database',
                    isShow: !isRdosDataSource
                },
                {
                    url: 'batch.html#/project',
                    isShow: !isRdosPro
                },
                {
                    url: 'batch.html#/data-manage',
                    isShow: !isRdosMap
                },
                {
                    url: 'batch.html#/data-model',
                    isShow: !isRdosModal
                },
                // stream
                {
                    url: 'stream.html',
                    isShow: !isStream
                },
                {
                    url: 'stream.html#/database',
                    isShow: !isStreamDataSource
                },
                {
                    url: 'stream.html#/realtime',
                    isShow: !isStreamTask
                },
                {
                    url: 'stream.html#/project',
                    isShow: !isStreamPro
                },
                {
                    url: 'stream.html#/operation',
                    isShow: !isStreamOpera
                },
                // analyticsEngine
                {
                    url: 'analytics.html',
                    isShow: !isAna
                },
                // dataQuality
                {
                    url: 'dataQuality.html',
                    isShow: !isQuali
                },
                {
                    url: 'dataQuality.html#/dq/overview',
                    isShow: !isQualiOver
                },
                {
                    url: 'dataQuality.html#/dq/taskQuery',
                    isShow: !isQualiTaskSearch
                },
                {
                    url: 'dataQuality.html#/dq/rule',
                    isShow: !isQualiRule
                },
                {
                    url: 'dataQuality.html#/dq/dataCheck',
                    isShow: !isQualiVali
                },
                {
                    url: 'dataQuality.html#/dq/dataSource',
                    isShow: !isQualiDataSource
                },
                // dataApi
                {
                    url: 'dataApi.html',
                    isShow: !isDataApi
                },
                {
                    url: 'dataApi.html#/api',
                    isShow: !isApiover
                },
                {
                    url: 'dataApi.html#/api/market',
                    isShow: !isApiMarket
                },
                {
                    url: 'dataApi.html#/api/mine',
                    isShow: !isApiMine
                },
                {
                    url: 'dataApi.html#/api/manage',
                    isShow: !isApiMana
                },
                {
                    url: 'dataApi.html#/api/approvalAndsecurity',
                    isShow: !isApiSafe
                },
                {
                    url: 'dataApi.html#/api/dataSource',
                    isShow: !isApiDataSource
                }
            ];
            this.loopIsIntercept(pathAddress, arr);
        }
        // 用户未上传license,返回空数组情况
        if (licenseApps && licenseApps.length == 0) {
            if (pathAddress.indexOf('index.html') == -1) {
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
        let { children, licenseApps } = this.props;
        if (!licenseApps || licenseApps.length === 0) {
            children = <GlobalLoading />
        }
        return children || <NotFund />
    }
}

Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
