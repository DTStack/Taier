import * as React from 'react';

import { Tooltip, Icon, Button, Popconfirm, Card } from 'antd';
import Copy from 'main/components/copy-icon';

import api from '../../../api/mine';
import docPath from '../../../consts/docPath';

class CallApi extends React.Component<any, any> {
    state: any = {
        appKey: '',
        appSecret: '',
        isShow: false,
        haveKey: true,
        loading: false
    }
    componentDidMount () {
        this.getUserKey();
    }
    resetKey = () => {
        api.resetUserKey().then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    appKey: res.data.appKey,
                    appSecret: res.data.appSecret
                })
            }
        })
    }
    generateKey = () => {
        this.setState({
            loading: true
        })
        api.generateSkInfo().then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code == 1) {
                this.setState({
                    appKey: res.data.appKey,
                    appSecret: res.data.appSecret,
                    haveKey: true
                })
            }
        })
    }
    getUserKey () {
        api.getUserKey().then((res: any) => {
            if (res.code == 1) {
                if (res.data) {
                    this.setState({
                        appKey: res.data.appKey,
                        appSecret: res.data.appSecret
                    })
                } else {
                    this.setState({
                        haveKey: false
                    })
                }
            }
        })
    }
    showOrHideSecret () {
        const { isShow } = this.state;
        this.setState({
            isShow: !isShow
        })
    }
    render () {
        const { appKey, appSecret, isShow, haveKey, loading } = this.state;
        let showAppSecret = isShow ? appSecret : '*'.repeat(appSecret.length);
        return (
            <div className='c-call-api'>
                <header className='c-call-api__header'>
                    <span className='c-call-api__header__title'>API调用认证</span>
                </header>
                <div className='c-call-api__content'>
                    <Card title={<span>方式一：AK/SK签名加密方式<Tooltip title='API调用时，需将APP Key、APP Secret加密后的签名填写在Header中进行调用，以便API调用具有更高的安全性'>
                        <Icon style={{ marginLeft: '6px' }} type="exclamation-circle-o" />
                    </Tooltip></span>} bordered={false} noHovering={true}>
                        <section className='c-call-api__content__section'>
                            <div className='c-call-api__section__title'>
                                <h1 className="title-border-l-blue">
                                    加密签名身份认证
                                </h1>
                            </div>
                            <div className='c-call-api__section__content'>
                                {haveKey ? (
                                    <React.Fragment>
                                        <div className='c-shadow-text'>
                                            <span className='c-shadow-text__prefix'>APP Key：</span>
                                            <span className='c-shadow-text__content'>{appKey}</span>
                                        </div>
                                        <div className='c-shadow-text'>
                                            <span className='c-shadow-text__prefix'>APP Secret：</span>
                                            {appSecret && (
                                                <React.Fragment>
                                                    <span className='c-shadow-text__content'>{showAppSecret}</span>
                                                    <span className='c-shadow-text__extra'>
                                                        <a onClick={this.showOrHideSecret.bind(this)}>{isShow ? '隐藏' : '显示'}</a>
                                                        <Copy
                                                            customView={(
                                                                <a style={{ marginLeft: '13px' }}>复制</a>
                                                            )}
                                                            copyText={appSecret}
                                                        />
                                                        <Popconfirm title="确定重置吗?" onConfirm={this.resetKey} okText="确认" cancelText="取消">
                                                            <a style={{ marginLeft: '13px' }}>重置</a>
                                                        </Popconfirm>
                                                    </span>
                                                </React.Fragment>
                                            )}
                                        </div>
                                    </React.Fragment>
                                ) : (<Button loading={loading} type='primary' onClick={this.generateKey}>生成</Button>)}
                            </div>
                        </section>
                        <section className='c-call-api__content__section'>
                            <div className='c-call-api__section__title'>
                                <h1 className="title-border-l-blue">APP Secret签名生成</h1>
                            </div>
                            <div className='c-call-api__section__content'>
                                <a target='blank' href={docPath.API_KEY}>查看签名生成示例</a>
                            </div>
                        </section>
                    </Card>
                    <Card title={<span>方式二：TOKEN加密方式<Tooltip title='API调用时，需将API-TOKEN填写至Header中进行调用，适合API测试、企业内部等安全性要求不高的使用场景。'>
                        <Icon style={{ marginLeft: '6px' }} type="exclamation-circle-o" />
                    </Tooltip></span>} bordered={false} noHovering={true}>
                        <section className='c-call-api__content__section'>
                            <div className='c-call-api__section__title'>
                                <h1 className="title-border-l-blue">TOKEN身份认证</h1>
                            </div>
                            <div className='c-call-api__section__content'>
                                每个API拥有一个API-TOKEN，可在“我的API-API详情”中查看。API-TOKEN支持重置。
                            </div>
                        </section>
                    </Card>
                </div>
            </div>
        )
    }
}

export default CallApi;
