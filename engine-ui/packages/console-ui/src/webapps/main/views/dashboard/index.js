import React, { Component } from 'react'
import { connect } from 'react-redux'
import { cloneDeep } from 'lodash';
import utils from 'utils';
import { Link } from 'react-router';
import { Alert } from 'antd';
import Api from '../../api';
import Header from '../layout/header'
import Footer from '../layout/footer';
import { getInitUser } from '../../actions/user'

import { MY_APPS, getThemeBanner } from '../../consts';
import '../../styles/views/portal.scss';

@connect(state => {
    return {
        licenseApps: state.licenseApps,
        apps: state.apps,
        user: state.user
    }
})
class Dashboard extends Component {
    state = {
        alertShow: false,
        alertMessage: ''
    }
    componentDidMount () {
        this._userLoaded = false;
        this.checkIsOverdue()
        // this.listenUserStatus();
    }

    listenUserStatus = () => {
        const { dispatch } = this.props;

        setInterval(() => {
            const id = utils.getCookie('dt_user_id');

            if (!this._userLoaded && id && id !== 0) {
                this._userLoaded = true;
                dispatch(getInitUser())
            }
        }, 1000);
    }
    /* eslint-disable */
    // 控制apps与licenseApps应用是否显示
    compareEnable = (apps, licenseApps) => {
        if (licenseApps && licenseApps.length > 1) {
            const newApps = cloneDeep(apps);
            newApps.map(item => {
                for (var key in item) {
                    licenseApps.map(itemLicen => {
                        for ( var key in itemLicen) {
                            if (item.id == itemLicen.id) {
                                item.enable = itemLicen.isShow
                                item.name = itemLicen.name
                            }
                        }
                    })
                }
            })
            return newApps
        } else {
            return []
        }
    }
    // 检查是否过期
    checkIsOverdue = () => {
        Api.checkisOverdue().then(res => {
            if (res.data && res.data.code === 1) {
                this.setState({
                    alertShow: true,
                    alertMessage: res.data.message
                })
            }
        })
    }
    renderApps = () => {
        const { apps, licenseApps, user } = this.props;
        const sections = this.compareEnable(apps, licenseApps).map(app => {
            const isShow = app.enable && (!app.needRoot || (app.needRoot && user.isRoot))

            return isShow && app.id !== MY_APPS.MAIN && (
                <a href={app.link} className="app-tag" key={app.id}>
                    <img className="app-logo" src={app.icon} />
                    <h1>{app.name}</h1>
                    <p>{app.description}</p>
                </a>
            )
        })

        return sections
    }

    render () {
        const showSummary = window.APP_CONF.showSummary;
        let summary = window.APP_CONF.summary;
        /**
         * 容错处理
         */
        if (showSummary && !summary) {
            console.error('summary配置不正确，请检查配置文件！');
            summary = {};
        }
        return (
            <div className="portal">
                <Header />
                <div className="container">
                    {this.state.alertShow ? (
                        <Alert
                            className='ant-alert_height'
                            message="请注意"
                            description={<span>{this.state.alertMessage}, 点击<Link to="http://dtuic.dtstack.net/#/licenseview" >立即申请</Link> </span>}
                            type="warning"
                            showIcon
                            closable
                        />
                    ) : null }
                    <div className={`c-banner ${window.APP_CONF.theme || 'default'}`}>
                        <div className="c-banner__content l-content">
                            <div className="c-banner__content__txt">
                                <h1>{window.APP_CONF.indexTitle}</h1>
                                <span>{window.APP_CONF.indexDesc}</span>
                            </div>
                            <div className="c-banner__content__img">
                                <img src={getThemeBanner()} />
                            </div>
                        </div>
                    </div>
                    <div className="applink l-content">
                        {showSummary && (
                            <section className='c-summary'>
                                <h className='c-summary__title'>
                                    {window.APP_CONF.summary.title}
                                </h>
                                <div className='c-summary__content'>
                                    {window.APP_CONF.summary.content}
                                </div>
                            </section>
                        )}
                        {this.renderApps()}
                    </div>
                    <Footer></Footer>
                </div>
            </div>
        )
    }
}

export default Dashboard
