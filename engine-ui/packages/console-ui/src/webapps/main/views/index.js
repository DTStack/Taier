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
            const isRdosShow = rdosApp.is_Show;
            const isRdosTask = rdosApp.children[0].is_Show;
            const isRdosOpera = rdosApp.children[1].is_Show;
            const isRdosDataSource = rdosApp.children[2].is_Show;
            const isRdosPro = rdosApp.children[3].is_Show;
            const isRdosMap = rdosApp.children[4].is_Show;
            const isRdosModal = rdosApp.children[5].is_Show;
            // streamAPP
            const streamApp = licenseApps[1];
            const isStream = streamApp.is_Show;
            const isStreamDataSource = streamApp.children[0].is_Show;
            const isStreamTask = streamApp.children[1].is_Show;
            const isStreamPro = streamApp.children[2].is_Show;
            const isStreamOpera = streamApp.children[3].is_Show;
            // analyticsEngine
            const analyApp = licenseApps[2];
            const isAna = analyApp.is_Show;
            // dataQuality
            const qualityApp = licenseApps[3];
            const isQuali = qualityApp.is_Show;
            const isQualiOver = qualityApp.children[0].is_Show;
            const isQualiTaskSearch = qualityApp.children[1].is_Show;
            const isQualiRule = qualityApp.children[2].is_Show;
            const isQualiVali = qualityApp.children[3].is_Show;
            const isQualiDataSource = qualityApp.children[4].is_Show;
            // dataApi
            const apiApp = licenseApps[4];
            const isDataApi = apiApp.is_Show;
            const isApiover = apiApp.children[0].is_Show;
            const isApiMarket = apiApp.children[1].is_Show;
            const isApiMine = apiApp.children[2].is_Show;
            const isApiMana = apiApp.children[3].is_Show;
            const isApiSafe = apiApp.children[4].is_Show;
            const isApiDataSource = apiApp.children[5].is_Show;

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
