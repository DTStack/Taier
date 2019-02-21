import React, { Component } from 'react'
import './style.scss'
import { chromeDownload } from 'consts/index';
export default class Index extends Component {
    renderDivide (name) {
        let divideLine = Array(25).fill('-');
        return <div>
            <span style={{ color: '#BFBFBF' }}>{divideLine.join(' ')}</span>
            <span className="dividerText">{name}</span>
            <span style={{ color: '#BFBFBF' }}>{divideLine.join(' ')}</span>
        </div>
    }
    detectOS () {
        let isWin = (navigator.platform == 'Win32') || (navigator.platform == 'Windows');
        let isMac = (navigator.platform == 'Mac68K') || (navigator.platform == 'MacPPC') || (navigator.platform == 'Macintosh') || (navigator.platform == 'MacIntel');
        if (isMac) return 'Mac';
        if (isWin) return 'Windows';
        return 'others';
    }
    downloadChrome () {
        let os = this.detectOS();
        window.open(chromeDownload[os], '_blank');
    }
    render () {
        return (
            <div className="lowVersion" style={{ height: '100%' }}>
                <img src='public/main/img/pic_update.png' className="pic_update" />
                <div style={{ fontSize: '14px', color: '#333333' }}>本产品当前可兼容Chrome66以及以上版本，请您更换至最新的谷歌(Chrome)浏览器</div>
                <div className="divide">
                    {this.renderDivide('点击即可下载，更新即刻搞定')}
                </div>
                <div className="download">
                    <a href="#" onClick={this.downloadChrome.bind(this)}>
                        <img src='public/main/img/btn_google.svg' />
                        <div className="chromeVersion">Chrome72.0.3626.109</div>
                    </a>
                </div>
            </div>
        )
    }
}
