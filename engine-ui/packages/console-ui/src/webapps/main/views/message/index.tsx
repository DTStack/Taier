import * as React from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { Link } from 'react-router'
import { Icon } from 'antd'

import NotFund from 'widgets/notFund'

import * as MsgActions from '../../actions/message'

import Navigator, { Title, MyIcon } from '../../components/nav'

import '../../styles/views/message.scss';

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        licenseApps: state.licenseApps,
        routing: state.routing,
        msgList: state.msgList
    }
}, (dispatch: any) => bindActionCreators(MsgActions, dispatch)) as any)
class MessageCenter extends React.Component<any, any> {
    componentDidMount () {}

    render () {
        const { apps, children, msgList, updateMsg, licenseApps, user } = this.props;
        const logo = (<Link to="/message">
            <MyIcon>
                <Icon type="message" />
            </MyIcon>
            <Title>消息中心</Title>
        </Link>)

        const content = children ? React.cloneElement(children as any, {
            msgList,
            updateMsg,
            apps,
            licenseApps
        }) : <NotFund />
        const settingMenus = [{
            id: 'admin/audit',
            name: '安全审计',
            link: `/admin/audit`,
            enable: user.isRoot,
            enableIcon: true,
            className: 'safeaudit'
        }];
        return (
            <div className="message">
                <Navigator
                    logo={logo}
                    menuItems={[]}
                    licenseApps={licenseApps}
                    settingMenus={settingMenus}
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
