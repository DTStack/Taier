import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import NotFund from 'widgets/notFund'
import { getLicenseApp } from '../actions/app'
import GlobalLoading from './layout/loading'
import ChromeDownload from 'widgets/chromeDownload';
import { getInitUser } from '../actions/user'
import userActions from '../consts/userActions'
import { initNotification } from 'funcs';
import http from '../api';
import { cloneDeep } from 'lodash';
import Header from './layout/header';
import utils from 'utils/index';
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
        for (let i = 0; i < arr.length; i++) {
            if (pathAddress.indexOf(arr[i].url) > -1 && arr[i].isShow) {
                window.location.href = '/';
                return;
            }
        }
    }
    // rdos
    fixRdosChildrenApps = (arr) => {
        let fixRdosChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '数据源':
                        fixRdosChildrenApps[0] = item;
                        break;
                    case '数据开发':
                        fixRdosChildrenApps[1] = item;
                        break;
                    case '运维中心':
                        fixRdosChildrenApps[2] = item;
                        break;
                    case '数据地图':
                        fixRdosChildrenApps[3] = item;
                        break;
                    case '数据模型':
                        fixRdosChildrenApps[4] = item;
                        break;
                    case '项目管理':
                        fixRdosChildrenApps[5] = item;
                        break;
                }
            })
            return fixRdosChildrenApps
        } else {
            return arr
        }
    }
    // stream
    fixStreamChildrenApps = (arr) => {
        let fixStreamChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '数据源':
                        fixStreamChildrenApps[0] = item;
                        break;
                    case '数据开发':
                        fixStreamChildrenApps[1] = item;
                        break;
                    case '任务运维':
                        fixStreamChildrenApps[2] = item;
                        break;
                    case '项目管理':
                        fixStreamChildrenApps[3] = item;
                        break;
                }
            })
            return fixStreamChildrenApps
        } else {
            return arr
        }
    }
    // analyticsEngine
    fixAnalyChildrenApps = (arr) => {
        let fixAnalyChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '数据库管理':
                        fixAnalyChildrenApps[0] = item;
                        break;
                    case '表管理':
                        fixAnalyChildrenApps[1] = item;
                        break;
                }
            })
            return fixAnalyChildrenApps
        } else {
            return arr
        }
    }
    // dataQuality
    fixQualityChildrenApps = (arr) => {
        let fixQualityChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '概览':
                        fixQualityChildrenApps[0] = item;
                        break;
                    case '任务查询':
                        fixQualityChildrenApps[1] = item;
                        break;
                    case '规则配置':
                        fixQualityChildrenApps[2] = item;
                        break;
                    case '逐行校验':
                        fixQualityChildrenApps[3] = item;
                        break;
                    case '数据源管理':
                        fixQualityChildrenApps[4] = item;
                        break;
                }
            })
            return fixQualityChildrenApps
        } else {
            return arr
        }
    }
    // Api
    fixApiChildrenApps = (arr) => {
        let fixApiChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '概览':
                        fixApiChildrenApps[0] = item;
                        break;
                    case 'API市场':
                        fixApiChildrenApps[1] = item;
                        break;
                    case '我的API':
                        fixApiChildrenApps[2] = item;
                        break;
                    case 'API管理':
                        fixApiChildrenApps[3] = item;
                        break;
                    case '授权与安全':
                        fixApiChildrenApps[4] = item;
                        break;
                    case '数据源管理':
                        fixApiChildrenApps[5] = item;
                        break;
                }
            })
            return fixApiChildrenApps
        } else {
            return arr;
        }
    }
    // license禁用app url 跳转到首页
    isEnableLicenseApp () {
        let { licenseApps } = this.props;
        const pathAddress = this.getCurrentPath();
        // 成功返回数据
        if (licenseApps && licenseApps.length) {
            let fixLicenseApps = cloneDeep(licenseApps)
            console.log('license', licenseApps)
            console.log('fixlicense', fixLicenseApps)
            fixLicenseApps[0].children = this.fixRdosChildrenApps(fixLicenseApps[0].children)
            fixLicenseApps[1].children = this.fixStreamChildrenApps(fixLicenseApps[1].children)
            fixLicenseApps[2].children = this.fixAnalyChildrenApps(fixLicenseApps[2].children)
            fixLicenseApps[3].children = this.fixQualityChildrenApps(fixLicenseApps[3].children)
            fixLicenseApps[4].children = this.fixApiChildrenApps(fixLicenseApps[4].children)
            // rdosAPP
            const rdosApp = fixLicenseApps[0];
            const isRdosShow = rdosApp.isShow;
            const isRdosDataSource = rdosApp.children[0] && rdosApp.children[0].isShow;
            const isRdosTask = rdosApp.children[1] && rdosApp.children[1].isShow;
            const isRdosOpera = rdosApp.children[2] && rdosApp.children[2].isShow;
            const isRdosMap = rdosApp.children[3] && rdosApp.children[3].isShow;
            const isRdosModal = rdosApp.children[4] && rdosApp.children[4].isShow;
            const isRdosPro = rdosApp.children[5] && rdosApp.children[5].isShow;
            // streamAPP
            const streamApp = fixLicenseApps[1];
            const isStream = streamApp.isShow;
            const isStreamDataSource = streamApp.children[0] && streamApp.children[0].isShow;
            const isStreamTask = streamApp.children[1] && streamApp.children[1].isShow;
            const isStreamOpera = streamApp.children[2] && streamApp.children[2].isShow;
            const isStreamPro = streamApp.children[3] && streamApp.children[3].isShow;
            // analyticsEngine
            const analyApp = fixLicenseApps[2];
            const isAna = analyApp.isShow;
            // dataQuality
            const qualityApp = fixLicenseApps[3];
            const isQuali = qualityApp.isShow;
            const isQualiOver = qualityApp.children[0] && qualityApp.children[0].isShow;
            const isQualiTaskSearch = qualityApp.children[1] && qualityApp.children[1].isShow;
            const isQualiRule = qualityApp.children[2] && qualityApp.children[2].isShow;
            const isQualiVali = qualityApp.children[3] && qualityApp.children[3].isShow;
            const isQualiDataSource = qualityApp.children[4] && qualityApp.children[4].isShow;
            // dataApi
            const apiApp = fixLicenseApps[4];
            const isDataApi = apiApp.isShow;
            const isApiover = apiApp.children[0] && apiApp.children[0].isShow;
            const isApiMarket = apiApp.children[1] && apiApp.children[1].isShow;
            const isApiMine = apiApp.children[2] && apiApp.children[2].isShow;
            const isApiMana = apiApp.children[3] && apiApp.children[3].isShow;
            const isApiSafe = apiApp.children[4] && apiApp.children[4].isShow;
            const isApiDataSource = apiApp.children[5] && apiApp.children[5].isShow;
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
        let lowVersionChrome = utils.isLowVersionChrome();
        if (!licenseApps) {
            children = <GlobalLoading />
        }
        if (lowVersionChrome) {
            children = <div>
                <Header />
                <ChromeDownload />
            </div>
        }
        return children || <NotFund />
    }
}

Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
