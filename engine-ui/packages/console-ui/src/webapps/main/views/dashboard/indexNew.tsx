import * as React from 'react'
import { connect } from 'react-redux'
import { cloneDeep, chain } from 'lodash';
import utils from 'utils';
// import { Link } from 'react-router';
import { Alert, Row, Col } from 'antd';
import Api from '../../api';
import Header from '../layout/newHeader'
import Footer from '../layout/newFooter';
import { getInitUser } from '../../actions/user'

import { MY_APPS, getThemeBanner } from '../../consts';
import { compareEnableApp } from '../../components/nav';
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
        const sections = chain(compareEnableApp(apps, licenseApps, false))
            .filter((app: any) => {
                const isShow = app.enable && (!app.needRoot || (app.needRoot && user.isRoot))
                return isShow && app.id !== MY_APPS.MAIN;
            })
            .map((app: any, index: any) => {
                console.log(app)
                return (
                    <Col
                        span={8}
                        style={{
                            marginTop: index > 2 ? '64px' : '28px'
                        }}
                        key={app.id}
                    >
                        <a href={app.link} className="app-new-tag" key={app.id}>
                            <img className="app-logo" src={app.newIcon} alt={app.name}/>
                            <div style={{ marginTop: '16px' }} className="app-new-title">{app.name}</div>
                            <p style={{ wordBreak: 'break-all', marginTop: '4px' }}>{app.description}</p>
                        </a>
                    </Col>
                )
            }).value()
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
                        top: '0px',
                        background: 'rgb(242, 247, 250)',
                        display: 'flex',
                        flexDirection: 'column'
                    }}
                >
                    <div
                        style={{
                            position: 'relative',
                            paddingBottom: '58px',
                            flex: 1
                        }}
                    >
                        <Header />
                        {this.state.alertShow ? (
                            <Alert
                                className='ant-alert_height'
                                message="请注意"
                                description={<span>{this.state.alertMessage}, 点击<a target="_blank" rel="noopener noreferrer" href={`${window.COMMON_CONF.UIC_URL}/#/licensemanage`} >立即申请</a> </span>}
                                type="warning"
                                showIcon
                                closable
                                style={{
                                    top: '50px',
                                    position: 'absolute',
                                    left: 0,
                                    right: 0,
                                    zIndex: 9999
                                }}
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
                                    // left: '150px'
                                    left: '50%',
                                    marginLeft: '-600px',
                                    zIndex: 9999
                                }}
                            >
                                <div
                                    style={{
                                        fontFamily: 'PingFangSC-Medium',
                                        fontSize: '42px',
                                        color: '#FFFFFF',
                                        letterSpacing: '2.33px'
                                    }}
                                >
                                    {window.APP_CONF.indexTitle}
                                </div>
                                <span
                                    style={{
                                        fontFamily: 'PingFangSC-Regular',
                                        fontSize: '24px',
                                        color: '#FFFFFF',
                                        letterSpacing: '1.33px',
                                        marginTop: '10px'
                                    }}
                                >{window.APP_CONF.indexDesc}</span>
                            </div>
                            <div
                                style={{
                                    position: 'relative',
                                    height: '600px',
                                    width: '100%',
                                    background: 'rgb(24, 25, 98)'
                                }}
                            >
                                <img
                                    src={getThemeBanner()}
                                    alt="背景图"
                                    style={{
                                        height: '600px',
                                        position: 'absolute',
                                        left: '50%',
                                        marginLeft: '-960px',
                                        zIndex: 1
                                    }}
                                />
                                <img
                                    src={'public/main/img/bg2.png'}
                                    alt="背景logo图"
                                    style={{
                                        height: '600px',
                                        position: 'absolute',
                                        left: '50%',
                                        marginLeft: '-600px',
                                        zIndex: 2
                                    }}
                                />
                            </div>
                        </div>
                        <div
                            style={{
                                width: '100%',
                                background: '#F2F7FA',
                                marginTop: '-120px'
                            }}
                        >
                            <div
                                style={{
                                    background: '#fff',
                                    boxShadow: '0px 0px 15px rgba(24, 39, 140, 0.3)',
                                    width: '1200px',
                                    margin: '0 auto',
                                    position: 'relative',
                                    zIndex: 9999
                                }}
                            >
                                <div
                                    className="applink l-content"
                                    style={{
                                        paddingTop: showSummary ? '30px' : '4px',
                                        width: '1140px'
                                    }}
                                >
                                    {showSummary && (
                                        <section className='c-summary' style={{ marginBottom: 0 }}>
                                            {
                                                window.APP_CONF.summary.title && (
                                                    <div className='c-newsummary__title'>
                                                        {window.APP_CONF.summary.title}
                                                    </div>
                                                )
                                            }
                                            {
                                                window.APP_CONF.summary.content && (
                                                    <div className='c-newsummary__content'>
                                                        {window.APP_CONF.summary.content}
                                                    </div>
                                                )
                                            }
                                        </section>
                                    )}
                                    <Row
                                        style={{
                                            width: '100%'
                                        }}
                                    >
                                        {this.renderApps()}
                                    </Row>
                                </div>
                            </div>
                        </div>
                        <Footer></Footer>
                    </div>
                </div>
            </div>
        )
    }
}

export default DashboardNew
