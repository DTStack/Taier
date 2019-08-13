import * as React from 'react'
import { connect } from 'react-redux'
import { cloneDeep } from 'lodash';
import utils from 'utils';
// import { Link } from 'react-router';
import { Alert, Row, Col } from 'antd';
import Api from '../../api';
import Header from '../layout/header'
import Footer from '../layout/footer';
import { getInitUser } from '../../actions/user'

import { MY_APPS, getThemeBanner } from '../../consts';
import '../../styles/views/portal.scss';

declare var window: any;

@(connect((state: any) => {
    return {
        licenseApps: state.licenseApps,
        apps: state.apps,
        user: state.user
    }
}) as any)
class DashboardNew extends React.Component<any, any> {
    _userLoaded: any;
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

            if (!this._userLoaded && id && id !== '0') {
                this._userLoaded = true;
                dispatch(getInitUser())
            }
        }, 1000);
    }
    // 控制apps与licenseApps应用是否显示
    compareEnable = (apps: any, licenseApps: any) => {
        if (licenseApps && licenseApps.length) {
            const newApps = cloneDeep(apps);
            for (let i = 0; i < newApps.length; i++) {
                for (let j = 0; j < licenseApps.length; j++) {
                    if (newApps[i].id == licenseApps[j].id) {
                        newApps[i].enable = licenseApps[j].isShow
                    }
                }
            }
            return newApps
        } else {
            return []
        }
    }
    // 检查是否过期
    checkIsOverdue = () => {
        Api.checkisOverdue().then((res: any) => {
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
        const sections = this.compareEnable(apps, licenseApps).map((app: any) => {
            const isShow = app.enable && (!app.needRoot || (app.needRoot && user.isRoot))

            return isShow && app.id !== MY_APPS.MAIN && (
                <Col
                    span={8}
                    style={{
                        marginTop: '20px'
                    }}
                >
                    <a href={app.link} className="app-new-tag" key={app.id}>
                        <img className="app-logo" src={app.newIcon}/>
                        <h1>{app.name}</h1>
                        <p style={{ wordBreak: 'break-all' }}>{app.description}</p>
                    </a>
                </Col>
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
                <div
                    className="container"
                    style={{
                        top: '0px'
                    }}
                >
                    <Header isNewHeader={true} />
                    {this.state.alertShow ? (
                        <Alert
                            className='ant-alert_height'
                            message="请注意"
                            description={<span>{this.state.alertMessage}, 点击<a target="_blank" rel="noopener noreferrer" href={`${window.COMMON_CONF.UIC_URL}/#/licensemanage`} >立即申请</a> </span>}
                            type="warning"
                            showIcon
                            closable
                        />
                    ) : null }
                    <div
                        style={{
                            position: 'relative',
                            width: '100%'
                        }}
                    >
                        <div
                            style={{
                                position: 'absolute',
                                top: '30%',
                                left: '150px'
                            }}
                        >
                            <h1 style={{ color: '#fff', fontSize: '36px', fontWeight: 'initial' }}>{window.APP_CONF.indexTitle}</h1>
                            <span
                                style={{
                                    color: '#fff',
                                    marginTop: '20px',
                                    fontSize: '22px'
                                }}
                            >{window.APP_CONF.indexDesc}</span>
                        </div>
                        <img
                            src={getThemeBanner()}
                            style={{
                                height: (document.body.offsetWidth * 0.3125),
                                width: '100%',
                                minHeight: '393.75px'
                            }}
                        />
                    </div>
                    <div
                        style={{
                            padding: '0 150px',
                            background: 'F2F7FA',
                            position: 'relative',
                            zIndex: 999
                        }}
                    >
                        <div
                            style={{
                                background: '#fff',
                                // boxShadow: '0px 0px 10px #696c7f',
                                boxShadow: '0px 0px 15px rgba(24, 39, 140, 0.3)',
                                marginTop: '-100px'
                            }}
                        >
                            <div
                                className="applink l-content"
                                style={{
                                    paddingTop: '30px'
                                }}
                            >
                                {showSummary && (
                                    <section className='c-summary'>
                                        <h1 className='c-summary__title'>
                                            {window.APP_CONF.summary.title}
                                        </h1>
                                        <div className='c-summary__content' style={{ color: '#333' }}>
                                            {window.APP_CONF.summary.content}
                                        </div>
                                    </section>
                                )}
                                <Row>
                                    {this.renderApps()}
                                </Row>
                            </div>
                            <Footer></Footer>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default DashboardNew
