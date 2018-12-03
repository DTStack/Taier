import React, { Component } from 'react'
import styled from 'styled-components';

class Footer extends Component {
    render () {
        const Footer = styled.footer`
            width: 100%;
            text-align: center;
            height: 40px;
            line-height: 40px;
            background: #fff;
            color: #999999;
            letter-spacing: 0.65px;
        `
        /* eslint-disable */
        return (
            <Footer className="footer">
                <p>{window.APP_CONF.showCopyright ? '©Copyright 2016-2018 杭州玳数科技有限公司 浙ICP备15044486号-1' : ''}版本：v{APP.VERSION}</p>
            </Footer>
        )
        /* eslint-disable */
    }
}

export default Footer
