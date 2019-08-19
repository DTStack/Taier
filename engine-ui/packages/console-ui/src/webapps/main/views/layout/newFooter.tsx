import styled from 'styled-components';
import * as React from 'react'

declare var window: any;
declare var APP: any;

class Footer extends React.Component<any, any> {
    render () {
        const Footer = styled.footer`
            width: 100%;
            text-align: center;
            color: #333;
            letter-spacing: 0.65px;
            padding: 20px 0;
        `
        /* eslint-disable */
        return (
            <Footer
                className="footer"
                style={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0
                }}
            >
                <p>{window.APP_CONF.showCopyright ? '©Copyright 2016-2018 杭州玳数科技有限公司 浙ICP备15044486号-1' : ''}版本：v{APP.VERSION}</p>
            </Footer>
        )
    }
}

export default Footer
