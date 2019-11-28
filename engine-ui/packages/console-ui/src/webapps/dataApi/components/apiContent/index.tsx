import * as React from 'react'
import moment from 'moment';
import { Popconfirm } from 'antd';
import './style.scss';
import RegisterSection from './register';
import CreateSection from './create';
import Webservice from './webservice';
import { showAdminMsg } from '../../components/adminMsgModal';
import Copy from 'main/components/copy-icon';
import { API_METHOD, API_METHOD_KEY } from '../../consts';
import { getApiMarketValue } from '../../utils';
import HelpDoc from '../../views/helpDoc'

class Content extends React.Component<any, any> {
    state: any = {
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
    getValue (key: any) {
        const { apiMarket, apiId } = this.props;
        return getApiMarketValue(key, apiMarket, apiId)
    }
    /**
     * api额外信息，例如当前市场订购信息
     * @param {*} key 属性名
     */
    getValueCallInfo (key: any) {
        const { apiMarket, apiId } = this.props;
        const api = apiMarket && apiMarket.api && apiMarket.apiCallInfo[apiId];

        if (api) {
            return api[key]
        } else {
            return null;
        }
    }

    resetKey = () => {
        const { resetToken, showRecord = {} } = this.props;
        resetToken(showRecord.id)
    }

    render () {
        const {
            apiId,
            callUrl,
            callLimit,
            beginTime,
            endTime,
            mode, // 管理模式/用户模式
            isRegister,
            apiVersionCode, // 是否使用TOKEN
            token, // token
            showMarketInfo, // 是否显示订购情况
            showUserInfo, // 是否显示用户个人的调用信息
            showReqLimit, // 是否显示调用限制
            registerInfo,
            showApiConfig, // 是否显示api的配置信息
            apiConfig = {} // api配置信息
        } = this.props;
        const isManage = mode == 'manage';
        const showExt = isManage;
        const isGET = this.getValue('reqMethod') == API_METHOD.GET
        let reqJson = this.getValue('reqJson');
        const apiPath = this.getValue('apiPath');
        console.log(this.props, this.props.apiMarket)
        if (isGET) {
            reqJson = Object.entries(reqJson).map(
                ([key, value]) => {
                    return `${key}=${value}`
                }
            ).join('&')
            reqJson = 'http(s)://调用URL' + (reqJson ? ('?' + reqJson) : '')
        }
        return (
            <div style={{ paddingBottom: '20px' }}>
                <section>
                    <h1 className="title-border-l-blue">基本信息</h1>
                    <div style={{ marginTop: 10 }}>
                        {!isRegister && (
                            <span data-title="支持格式：" className="pseudo-title p-line api_item-margin">{this.getValue('supportType')}</span>
                        )}
                        <span data-title="请求协议：" className="pseudo-title p-line api_item-margin">{this.getValue('reqProtocol')}</span>
                        <span data-title="请求方式：" className="pseudo-title p-line api_item-margin">{API_METHOD_KEY[this.getValue('reqMethod')]}</span>
                        <p data-title="API ID：" className="pseudo-title p-line">{apiId}</p>
                        {isRegister && isManage && (
                            <React.Fragment>
                                <p data-title="后端Host：" className="pseudo-title p-line">{registerInfo.originalHost}</p>
                                <p data-title="后端Path：" className="pseudo-title p-line">{registerInfo.originalPath}</p>
                            </React.Fragment>
                        )}
                        {apiPath && (
                            <p data-title="API path：" className="pseudo-title p-line">{apiPath}</p>
                        )}
                        {showReqLimit && (
                            <p data-title="调用次数限制：" className="pseudo-title p-line">{this.getValue('reqLimit')} 次/秒</p>
                        )}
                        {showUserInfo && <div>
                            <p data-title="调用URL：" className="pseudo-title p-line">{callUrl}</p>
                            <p data-title="API-TOKEN：" className="pseudo-title p-line">
                                {token}
                                <span>
                                    <Copy
                                        customView={(
                                            <a style={{ marginLeft: '13px' }}>复制</a>
                                        )}
                                        copyText={token}
                                    />
                                    <Popconfirm title="确定重置吗?" onConfirm={this.resetKey} okText="确认" cancelText="取消">
                                        <a style={{ marginLeft: '13px' }}>重置</a>
                                    </Popconfirm>
                                </span>
                            </p>
                            <p data-title="支持调用次数：" className="pseudo-title p-line">
                                {callLimit == -1 ? '无限制' : callLimit}
                                <a style={{ marginLeft: '5px' }} onClick={() => {
                                    showAdminMsg(apiId)
                                }}>
                                    申请修改
                                </a>
                            </p>
                            <p data-title="支持调用周期：" className="pseudo-title p-line">
                                {beginTime ? `${moment(beginTime).format('YYYY-MM-DD')} ~ ${moment(endTime).format('YYYY-MM-DD')}` : '无限制'}
                                <a style={{ marginLeft: '5px' }} onClick={() => {
                                    showAdminMsg(apiId)
                                }}>
                                    申请修改
                                </a>
                            </p>
                        </div>
                        }

                        <p data-title="API描述：" className="pseudo-title p-line">{this.getValue('desc')}</p>
                        <p data-title="创建人：" className="pseudo-title p-line">{this.getValue('createUser')}</p>
                        {showExt && (
                            <div>
                                <p data-title="最近修改人：" className="pseudo-title p-line">{this.getValue('modifyUser')}</p>
                                <p data-title="最近修改时间：" className="pseudo-title p-line">{moment(this.getValue('gmtModified')).format('YYYY-MM-DD HH:mm:ss')}</p>
                            </div>
                        )}
                        <p data-title="备注：" className="pseudo-title p-line">
                            {apiVersionCode && apiVersionCode === 'API_WITH_TOKEN' ? <span>API-TOKEN（{token}）<HelpDoc doc="tokenSpecification" /></span> : null }
                        </p>
                    </div>
                </section>
                {showMarketInfo && <section style={{ marginTop: 19.3 }}>
                    <h1 className="title-border-l-blue">调用订购情况</h1>
                    <div style={{ marginTop: 10 }}>
                        <span data-title="累计调用次数：" className="pseudo-title p-line api_item-margin">{this.getValueCallInfo('apiCallNum')}</span>
                        <span data-title="订购人数：" className="pseudo-title p-line api_item-margin">{this.getValueCallInfo('applyNum')}</span>
                    </div>
                </section>}
                {isRegister ? (
                    this.getValue('reqProtocol') === 'WebService'
                        ? (
                            <Webservice
                                isManage={isManage}
                                getValue={this.getValue.bind(this)}
                                registerInfo={registerInfo}
                            />
                        )
                        : (
                            <RegisterSection
                                isManage={isManage}
                                getValue={this.getValue.bind(this)}
                                registerInfo={registerInfo}
                            />
                        )
                ) : (<CreateSection
                    showApiConfig={showApiConfig}
                    apiConfig={apiConfig}
                    reqJson={reqJson}
                    isGET={isGET}
                    getValue={this.getValue.bind(this)}
                    getRequestDataSource={this.getRequestDataSource.bind(this)}
                    getResponseDataSource={this.getResponseDataSource.bind(this)}
                />)}
            </div>
        )
    }
}

export default Content;
