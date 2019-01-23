import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router'
import NotFund from 'widgets/notFund'
import { getLicenseApp } from '../actions/app'
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
        licenseApps: state.licenseApps
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
    /* eslint-disable */
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { user } = nextProps;

        if (this.props.user.dtuicUserId != user.dtuicUserId && user.dtuicUserId) {
            this.checkRoot(user);
        }
    }
    /* eslint-disable */

    // license禁用app url 跳转到首页
    isEnableLicenseApp () {
        const { licenseApps } = this.props;
        console.log(licenseApps)
        if (licenseApps.length > 1) {
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

            if (!isRdosShow || !isRdosTask || !isRdosOpera || !isRdosDataSource ||
                !isRdosPro || !isRdosMap || !isRdosModal || !isStream || !isStreamDataSource ||
                !isStreamTask || !isStreamPro || !isStreamOpera || !isAna || !isQuali ||
                !isQualiOver || !isQualiTaskSearch || !isQualiRule || !isQualiVali || !isQualiDataSource || !isDataApi ||
                !isApiover || !isApiMarket || !isApiMine || !isApiMana || !isApiSafe || !isApiDataSource) {
                    hashHistory.push('/');
            }
        }
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
        return this.props.children || <NotFund />
    }
}

Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
