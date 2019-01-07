import React, { Component } from 'react'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from '../layout/header'
import Footer from '../layout/footer';
import { getInitUser } from '../../actions/user'

import { MY_APPS, getThemeBanner } from '../../consts';
import '../../styles/views/portal.scss';

@connect(state => {
    return {
        apps: state.apps,
        user: state.user
    }
})
class Dashboard extends Component {
    componentDidMount () {
        this._userLoaded = false;
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

    renderApps = () => {
        const { apps, user } = this.props;
        const sections = apps.map(app => {
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
