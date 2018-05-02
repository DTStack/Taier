import React, { Component } from 'react'
import styled from 'styled-components';

class Footer extends Component {

    
    render() {
        const Footer = styled.footer`
            width: 100%;
            text-align: center;
            height: 40px;
            line-height: 40px;
            background: #fff;
            color: #999999;
            letter-spacing: 0.65px;
        `
        return (
            <Footer className="footer">
               <p>©Copyright 2016-2018 杭州玳数科技有限公司 浙ICP备15044486号-1</p>
            </Footer>
        )
    }
}

export default Footer