import styled from 'styled-components';
import * as React from 'react'

declare var window: any;
declare var APP: any;

class Footer extends React.Component<any, any> {
    render () {
        const Footer = styled.footer`
            width: 100%;
            text-align: center;
            height: 40px;
            line-height: 40px;
            background: #fff;
            color: #333;
            letter-spacing: 0.65px;
        `
        return (
            <Footer className="footer">
                <p>{window.APP_CONF.showCopyright ? `©Copyright 2016-${new Date().getUTCFullYear()} 杭州玳数科技有限公司 浙ICP备15044486号-1` : ''} 版本：v{APP.VERSION}</p>
            </Footer>
        )
    }
}

export default Footer
