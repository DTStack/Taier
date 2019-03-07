import React from 'react';

import { Tooltip, Icon } from 'antd';
import Copy from 'main/components/copy-icon';

class CallApi extends React.Component {
    state = {
        appKey: '25710890',
        appSecret: '8easjnqh23u283aishdjaiqwrbkfjaf',
        isShow: false
    }
    showOrHideSecret () {
        const { isShow } = this.state;
        this.setState({
            isShow: !isShow
        })
    }
    render () {
        const { appKey, appSecret, isShow } = this.state;
        let showAppSecret = isShow ? appSecret : '*'.repeat(appSecret.length);
        return (
            <div className='c-call-api'>
                <header className='c-call-api__header'>
                    <span className='c-call-api__header__title'>API调用认证</span>
                </header>
                <div className='c-call-api__content'>
                    <section className='c-call-api__content__section'>
                        <div className='c-call-api__section__title'>
                            <h1 className="title-border-l-blue">
                                加密签名身份认证
                                <Tooltip title='API调用时，需将APP Key、APP Secret加密后的签名填写在Header中进行调用，以便API调用具有更高的安全性'>
                                    <Icon style={{ marginLeft: '6px' }} type="exclamation-circle-o" />
                                </Tooltip>
                            </h1>
                        </div>
                        <div className='c-call-api__section__content'>
                            <div className='c-shadow-text'>
                                <span className='c-shadow-text__prefix'>APP key：</span>
                                <span className='c-shadow-text__content'>{appKey}</span>
                            </div>
                            <div className='c-shadow-text'>
                                <span className='c-shadow-text__prefix'>APP Secret：</span>
                                <span className='c-shadow-text__content'>{showAppSecret}</span>
                                <span className='c-shadow-text__extra'>
                                    <a onClick={this.showOrHideSecret.bind(this)}>{isShow ? '隐藏' : '显示'}</a>
                                    <Copy
                                        customView={(
                                            <a style={{ marginLeft: '13px' }}>复制</a>
                                        )}
                                        copyText={appSecret}
                                    />
                                    <a style={{ marginLeft: '13px' }}>重置</a>
                                </span>
                            </div>
                        </div>
                    </section>
                    <section className='c-call-api__content__section'>
                        <div className='c-call-api__section__title'>
                            <h1 className="title-border-l-blue">APP secret签名生成</h1>
                        </div>
                        <div className='c-call-api__section__content'>
                            <a>查看签名生成示例</a>
                        </div>
                    </section>
                </div>
            </div>
        )
    }
}

export default CallApi;
