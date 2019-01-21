import React, { Component } from 'react'
import moment from 'moment';

import './style.scss';
import SecurityDetailModal from '../../components/securityDetailModal';
import RegisterSection from './register';
import CreateSection from './create';

import { API_METHOD, API_METHOD_KEY } from '../../consts';
import { getApiMarketValue } from '../../utils';

class Content extends Component {
    state = {
        securityModalVisible: false,
        securityData: {}
    }
    /**
     * 请求参数
     */
    getRequestDataSource () {
        return this.getValue('reqParam') || [];
    }
    /**
     * 返回参数
     */
    getResponseDataSource () {
        return this.getValue('respParam') || [];
    }
    /**
     * api详细信息
     * @param {*} key 属性名
     */
    getValue (key) {
        const { apiMarket, apiId } = this.props;
        return getApiMarketValue(key, apiMarket, apiId)
    }
    /**
     * api额外信息，例如当前市场订购信息
     * @param {*} key 属性名
     */
    getValueCallInfo (key) {
        const { apiMarket, apiId } = this.props;
        const api = apiMarket && apiMarket.api && apiMarket.apiCallInfo[apiId];

        if (api) {
            return api[key]
        } else {
            return null;
        }
    }
    showSecurityDetail (security) {
        this.setState({
            securityModalVisible: true,
            securityData: security
        })
    }
    renderSecurityListView (securityList) {
        return securityList.map((security) => {
            return <a onClick={this.showSecurityDetail.bind(this, security)} key={security.id}>{security.name}</a>
        }).reduce((arrs, currentArr) => {
            if (arrs.length) {
                return arrs.concat(['，', currentArr])
            } else {
                return arrs.concat(currentArr)
            }
        }, [])
    }
    render () {
        const {
            callUrl,
            callLimit,
            beginTime,
            endTime,
            mode, // 管理模式/用户模式
            isRegister,
            showRecord,
            showMarketInfo, // 是否显示订购情况
            showUserInfo, // 是否显示用户个人的调用信息
            showSecurity, // 是否显示安全组
            securityList,
            showApiConfig, // 是否显示api的配置信息
            apiConfig = {} // api配置信息
        } = this.props;
        const { securityData, securityModalVisible } = this.state;
        const isManage = mode == 'manage';
        const showExt = isManage;
        const isGET = this.getValue('reqMethod') == API_METHOD.GET
        let reqJson = this.getValue('reqJson');

        if (isGET) {
            reqJson = Object.entries(reqJson).map(
                ([key, value]) => {
                    return `${key}=${value}`
                }
            ).join('&')
            reqJson = reqJson ? ('?' + reqJson) : '无'
        }
        return (
            <div style={{ paddingBottom: '20px' }}>
                <section>
                    <h1 className="title-border-l-blue">基本信息</h1>
                    <div style={{ marginTop: 10 }}>
                        {!isRegister && (
                            <span data-title="支持格式：" className="pseudo-title p-line api_item-margin">{this.getValue('supportType')}</span>
                        )}
                        {isRegister && isManage && (
                            <React.Fragment>
                                <span data-title="后端Host：" className="pseudo-title p-line api_item-margin">{this.getValue('reqProtocol')}</span>
                                <span data-title="后端Path：" className="pseudo-title p-line api_item-margin">{this.getValue('reqProtocol')}</span>
                            </React.Fragment>
                        )}
                        <span data-title="请求协议：" className="pseudo-title p-line api_item-margin">{this.getValue('reqProtocol')}</span>
                        <span data-title="请求方式：" className="pseudo-title p-line api_item-margin">{API_METHOD_KEY[this.getValue('reqMethod')]}</span>
                        <p data-title="API path：" className="pseudo-title p-line">{this.getValue('apiPath')}</p>
                        {!showExt && (
                            <p data-title="调用次数限制：" className="pseudo-title p-line">{this.getValue('reqLimit')} 次/秒</p>
                        )}
                        {showSecurity && (
                            <p data-title="安全组：" className="pseudo-title p-line">{this.renderSecurityListView(securityList)}</p>
                        )}
                        {showUserInfo && <div>
                            <p data-title="调用URL：" className="pseudo-title p-line">{callUrl}</p>
                            <p data-title="申请调用次数：" className="pseudo-title p-line">{callLimit == -1 ? '无限制' : callLimit}</p>
                            <p data-title="申请调用周期：" className="pseudo-title p-line">{beginTime ? `${moment(beginTime).format('YYYY-MM-DD')} ~ ${moment(endTime).format('YYYY-MM-DD')}` : '无限制'}</p>
                        </div>
                        }

                        <p data-title="API描述：" className="pseudo-title p-line">{this.getValue('desc')}</p>
                        <p data-title="创建人：" className="pseudo-title p-line">{this.getValue('createUser')}</p>
                        {showExt && (
                            <div>
                                <p data-title="最近修改人：" className="pseudo-title p-line">{showRecord.modifyUser}</p>
                                <p data-title="最近修改时间：" className="pseudo-title p-line">{moment(showRecord.gmtModified).format('YYYY-MM-DD HH:mm:ss')}</p>
                            </div>
                        )}
                    </div>
                </section>
                {isRegister ? (
                    <RegisterSection
                        isManage={isManage}
                    />
                ) : (<CreateSection
                    showApiConfig={showApiConfig}
                    showMarketInfo={showMarketInfo}
                    apiConfig={apiConfig}
                    reqJson={reqJson}
                    isGET={isGET}
                    getValue={this.getValue.bind(this)}
                    getValueCallInfo={this.getValueCallInfo.bind(this)}
                    getRequestDataSource={this.getRequestDataSource.bind(this)}
                    getResponseDataSource={this.getResponseDataSource.bind(this)}
                />)}
                <SecurityDetailModal
                    data={securityData}
                    visible={securityModalVisible}
                    closeModal={() => {
                        this.setState({
                            securityModalVisible: false
                        })
                    }}
                />
            </div>
        )
    }
}

export default Content;
