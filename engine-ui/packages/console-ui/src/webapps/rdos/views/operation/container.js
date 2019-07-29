import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout, Button, Icon } from 'antd'
import { connect } from 'react-redux'

import Sidebar from './sidebar'
import * as UserAction from '../../store/modules/user'
import { getTaskTypes } from '../../store/modules/offlineTask/comm';

const { Sider, Content } = Layout;
const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

class Container extends Component {
    state = {
        collapsed: false,
        mode: 'inline'
    };

    componentDidMount () {
        this.initUsers(this.props.project);
        this.props.dispatch(getTaskTypes());
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const { project = {} } = nextProps;
        const { project: old_project = {} } = this.props;
        if (old_project.id != project.id) {
            console.log(old_project.id, project.id)
            this.initUsers(project);
        }
    }
    initUsers (project) {
        const { id } = project;
        if (id) {
            this.props.dispatch(UserAction.getProjectUsers());
        }
    }

    toggleCollapsed = () => {
        this.setState({
            collapsed: !this.state.collapsed,
            mode: !this.state.collapsed ? 'vertical' : 'inline'
        });
    }

    render () {
        const { children } = this.props
        return (
            <Layout className="dt-operation">
                <Sider className="bg-w ant-slider-pos"
                    collapsed={this.state.collapsed}
                >
                    <Button className="ant-slider-pos--collapsed" onClick={this.toggleCollapsed}>
                        <Icon type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'} />
                    </Button>
                    <Sidebar {...this.props} mode={this.state.mode} />
                </Sider>
                <Content>
                    { children || "i'm container." }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default connect(state => {
    return {
        project: state.project
    }
})(Container)
