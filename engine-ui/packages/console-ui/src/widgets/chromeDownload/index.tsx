import * as React from 'react'
import utils from 'utils/index';
import './style.scss'
declare var window: any;
export default class Index extends React.Component<any, any> {
    renderDivide (name: any) {
        let divideLine = Array(25).fill('-');
        return <div>
            <span style={{ color: '#BFBFBF' }}>{divideLine.join(' ')}</span>
            <span className="divider-text">{name}</span>
            <span style={{ color: '#BFBFBF' }}>{divideLine.join(' ')}</span>
        </div>
    }
    detectOS () {
        if (utils.isMacOs()) return 'macChrome';
        if (utils.isWindows()) return 'windowsChrome';
        return 'others';
    }
    downloadChrome () {
        let os = this.detectOS();
        window.open(window.COMMON_CONF[os], '_blank');
    }
    render () {
        return (
            <div className="chrome-download" style={{ height: '100%' }}>
                <img src='public/main/img/pic_update.png' className="pic-update" />
                <div style={{ fontSize: '14px', color: '#333333' }}>本产品当前可兼容Chrome66以及以上版本，请您更换至最新的谷歌(Chrome)浏览器</div>
                <div className="divide">
                    {this.renderDivide('点击即可下载，更新即刻搞定')}
                </div>
                <div className="download">
                    <a href="#" onClick={this.downloadChrome.bind(this)}>
                        <img src='public/main/img/btn_google.svg' />
                        <div className="chrome-version">Chrome72.0.3626.109</div>
                    </a>
                </div>
            </div>
        )
    }
}
