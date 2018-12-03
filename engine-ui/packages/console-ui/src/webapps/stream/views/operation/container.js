import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'
import { connect } from 'react-redux'

import * as UserAction from '../../store/modules/user'

const { Content } = Layout;

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
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
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
    onCollapse = (collapsed) => {
        this.setState({
            collapsed,
            mode: collapsed ? 'vertical' : 'inline'
        });
    }

    render () {
        const { children } = this.props
        return (
            <div className="dt-operation">
                <Content>
                    { children || "i'm container." }
                </Content>
            </div>
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
