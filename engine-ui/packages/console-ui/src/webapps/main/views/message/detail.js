import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Icon } from 'antd'

import GoBack from '../../components/go-back'
import { tabBarStyle } from '../../consts'

import DataSouceApi from 'dataQuality/api/dataSource'

class MsgDetail extends Component {

    state = {
        active: 'all',
        msgInfo: '',
    }

    componentDidMount() {
        const { msgId } = this.props.router.params
        this.loadMsg(msgId);
    }

    loadMsg = (msgId) => {
        DataSouceApi.getDataSources({ msgId }).then(res => {
            this.setState({
                msgInfo: res.data,
            })
        })
    }


    render() {
        return (
            <div className="box-1">
                <div className="box-card msg-box">
                    <main>
                        <h1 className="card-title"><GoBack /> 蚂蚁金服设计平台简介</h1>
                        <p>
                            段落示意：蚂蚁金服设计平台 design.alipay.com，用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。蚂蚁金服设计平台 design.alipay.com，
                            用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。
                        </p>
                    </main>
                    <footer>
                        <span>
                            <Icon type="notification" />
                            发送于 {`2017-10-10 18: 00`}
                        </span>
                    </footer>
                </div>
            </div>
        )
    }
}

export default MsgDetail