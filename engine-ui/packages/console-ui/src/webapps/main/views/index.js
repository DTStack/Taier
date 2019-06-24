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
import { cloneDeep, get } from 'lodash';
import Header from './layout/header';
import utils from 'utils/index';
import * as apps from 'config/base';

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
        routing: state.routing,
        isLicenseLoaded: state.isLicenseLoaded
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
                    case '运维中心':
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
    fixScienceChildrenApps = (arr) => {
        let fixScienceChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '算法实验':
                        fixScienceChildrenApps[0] = item;
                        break;
                    case '运维中心':
                        fixScienceChildrenApps[1] = item;
                        break;
                    case '数据管理':
                        fixScienceChildrenApps[2] = item;
                        break;
                }
            })
            return fixScienceChildrenApps
        } else {
            return arr;
        }
    }
    // license禁用app url 跳转到首页
    isEnableLicenseApp () {
        let { licenseApps, isLicenseLoaded } = this.props;
        const pathAddress = this.getCurrentPath();
        // 成功返回数据
        if (licenseApps && licenseApps.length) {
            let fixLicenseApps = cloneDeep(licenseApps);
            let licenseMap = {};
            console.log('license', licenseApps)
            console.log('fixlicense', fixLicenseApps)
            fixLicenseApps.forEach((licenseApp) => {
                let newChildren = [];
                switch (licenseApp.id) {
                    case apps.rdosApp.id: {
                        newChildren = this.fixRdosChildrenApps(licenseApp.children);
                        break;
                    }
                    case apps.streamApp.id: {
                        newChildren = this.fixStreamChildrenApps(licenseApp.children)
                        break;
                    }
                    case apps.aeApp.id: {
                        newChildren = this.fixAnalyChildrenApps(licenseApp.children)
                        break;
                    }
                    case apps.dqApp.id: {
                        newChildren = this.fixQualityChildrenApps(licenseApp.children)
                        break;
                    }
                    case apps.daApp.id: {
                        newChildren = this.fixApiChildrenApps(licenseApp.children)
                        break;
                    }
                    case apps.scienceApp.id: {
                        newChildren = this.fixScienceChildrenApps(licenseApp.children)
                        break;
                    }
                }
                licenseMap[licenseApp.id] = {
                    ...licenseApp,
                    children: newChildren
                }
            });
            // rdosAPP
            const rdosApp = licenseMap[apps.rdosApp.id];
            const isRdosShow = rdosApp.isShow;
            const isRdosDataSource = get(rdosApp, 'children[0].isShow');
            const isRdosTask = get(rdosApp, 'children[1].isShow');
            const isRdosOpera = get(rdosApp, 'children[2].isShow');
            const isRdosMap = get(rdosApp, 'children[3].isShow');
            const isRdosModal = get(rdosApp, 'children[4].isShow');
            const isRdosPro = get(rdosApp, 'children[5].isShow');
            // streamAPP
            const streamApp = licenseMap[apps.streamApp.id];
            const isStream = streamApp.isShow;
            const isStreamDataSource = get(streamApp, 'children[0].isShow');
            const isStreamTask = get(streamApp, 'children[1].isShow');
            const isStreamOpera = get(streamApp, 'children[2].isShow');
            const isStreamPro = get(streamApp, 'children[3].isShow');
            // analyticsEngine
            const analyApp = licenseMap[apps.aeApp.id];
            const isAna = analyApp.isShow;
            // dataQuality
            const qualityApp = licenseMap[apps.dqApp.id];
            const isQuali = qualityApp.isShow;
            const isQualiOver = get(qualityApp, 'children[0].isShow');
            const isQualiTaskSearch = get(qualityApp, 'children[1].isShow');
            const isQualiRule = get(qualityApp, 'children[2].isShow');
            const isQualiVali = get(qualityApp, 'children[3].isShow');
            const isQualiDataSource = get(qualityApp, 'children[4].isShow');
            // dataApi
            const apiApp = licenseMap[apps.daApp.id];
            const isDataApi = apiApp.isShow;
            const isApiover = get(apiApp, 'children[0].isShow');
            const isApiMarket = get(apiApp, 'children[1].isShow');
            const isApiMine = get(apiApp, 'children[2].isShow');
            const isApiMana = get(apiApp, 'children[3].isShow');
            const isApiSafe = get(apiApp, 'children[4].isShow');
            const isApiDataSource = get(apiApp, 'children[5].isShow');
            // science
            const scienceApp = licenseMap[apps.scienceApp.id];
            const isScience = scienceApp.isShow;
            const isScienceDevelop = get(scienceApp, 'children[0].isShow');
            const isScienceOperation = get(scienceApp, 'children[1].isShow');
            const isScienceSource = get(scienceApp, 'children[2].isShow');
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
                },
                {
                    url: 'science.html',
                    isShow: !isScience
                },
                {
                    url: 'science.html#/science/workbench',
                    isShow: !isScienceDevelop
                },
                {
                    url: 'science.html#/science/operation',
                    isShow: !isScienceOperation
                },
                {
                    url: 'science.html#/science/source',
                    isShow: !isScienceSource
                }
            ];
            this.loopIsIntercept(pathAddress, arr);
        }
        // 用户未上传license,返回空数组情况
        if (isLicenseLoaded && licenseApps && licenseApps.length == 0) {
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
        let browserCheck = utils.browserCheck();
        if (!licenseApps) {
            children = <GlobalLoading />
        }
        if (!browserCheck) {
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
