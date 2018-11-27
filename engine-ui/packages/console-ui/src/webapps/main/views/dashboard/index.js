import React, { Component } from 'react'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from '../layout/header'
import Footer from '../layout/footer';
import { getInitUser } from '../../actions/user'

import { MY_APPS } from '../../consts';
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
        const { children } = this.props

        return (
            <div className="portal">
                <Header />
                <div className="container">
                    <div className="banner">
                        <div className="middle">
                            <div className="left txt">
                                <h1>{window.APP_CONF.indexTitle}</h1>
                                <span>企业级一站式数据中台-让数据产生价值</span>
                            </div>
                            <div className="left img">
                                <img src="/public/main/img/pic_banner.png" />
                            </div>
                        </div>
                    </div>
                    <div className="applink middle">
                        {this.renderApps()}
                    </div>
                    <Footer></Footer>
                </div>
            </div>
        )
    }
}

export default Dashboard
