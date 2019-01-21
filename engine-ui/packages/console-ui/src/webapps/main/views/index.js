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
        // rdos
        const isRdosShow = licenseApps[0].is_Show;
        const isRdosTask = licenseApps[0].children[0].is_Show;
        const isRdosOpera = licenseApps[0].children[1].is_Show;
        const isRdosDataSource = licenseApps[0].children[2].is_Show;
        const isRdosPro = licenseApps[0].children[3].is_Show;
        const isRdosMap = licenseApps[0].children[4].is_Show;
        const isRdosModal = licenseApps[0].children[5].is_Show;
        // stream
        const isStream = licenseApps[1].is_Show;
        const isStreamDataSource = licenseApps[1].children[0].is_Show;
        const isStreamTask = licenseApps[1].children[1].is_Show;
        const isStreamPro = licenseApps[1].children[2].is_Show;
        const isStreamOpera = licenseApps[1].children[3].is_Show;
        // analyticsEngine
        const isAna = licenseApps[2].is_Show;
        // dataQuality
        const isQuali = licenseApps[3].is_Show;
        const isQualiOver = licenseApps[3].children[0].is_Show;
        const isQualiTaskSearch = licenseApps[3].children[1].is_Show;
        const isQualiRule = licenseApps[3].children[2].is_Show;
        const isQualiVali = licenseApps[3].children[3].is_Show;
        const isQualiDataSource = licenseApps[3].children[4].is_Show;
        // dataApi
        const isDataApi = licenseApps[4].is_Show;
        const isApiover = licenseApps[4].children[0].is_Show;
        const isApiMarket = licenseApps[4].children[1].is_Show;
        const isApiMine = licenseApps[4].children[2].is_Show;
        const isApiMana = licenseApps[4].children[3].is_Show;
        const isApiSafe = licenseApps[4].children[4].is_Show;
        const isApiDataSource = licenseApps[4].children[5].is_Show;

        if (!isRdosShow || !isRdosTask || !isRdosOpera || !isRdosDataSource ||
            !isRdosPro || !isRdosMap || !isRdosModal || !isStream || !isStreamDataSource ||
            !isStreamTask || !isStreamPro || !isStreamOpera || !isAna || !isQuali ||
            !isQualiOver || !isQualiTaskSearch || !isQualiRule || !isQualiVali || !isQualiDataSource || !isDataApi ||
            !isApiover || !isApiMarket || !isApiMine || !isApiMana || !isApiSafe || !isApiDataSource) {
                hashHistory.push('/');
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
