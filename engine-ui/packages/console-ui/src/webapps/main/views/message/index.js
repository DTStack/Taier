import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { Link } from 'react-router'
import { Icon } from 'antd'

import utils from 'utils'
import NotFund from 'widgets/notFund'

import Header from './../layout/header'
import * as MsgActions from '../../actions/message'

import { Navigator, Logo, Title, MyIcon }  from '../../components/nav'

import '../../styles/views/message.scss';

@connect(state => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        msgList: state.msgList,
    }
}, dispatch => bindActionCreators(MsgActions, dispatch))
class MessageCenter extends Component {

    componentDidMount() {}

    render() {
        const { user, apps, children, msgList, updateMsg } = this.props;
        const logo = (<Link to="/message">
            <MyIcon>
                <Icon type="message" />
            </MyIcon>
            <Title>消息中心</Title>
        </Link>)

        const content = children ? React.cloneElement(children, {
            msgList,
            updateMsg,
            apps,
        }) : <NotFund /> 

        return (
            <div className="message">
                <Navigator 
                    logo={logo}
                    menuItems={[]}
                    {...this.props}
                />
                <div className="container">
                    { content }
                </div>
            </div>
        )
    }
}

export default MessageCenter
