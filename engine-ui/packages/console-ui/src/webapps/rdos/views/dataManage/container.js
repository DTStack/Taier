import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Layout } from 'antd'
import { connect } from 'react-redux';

import Sidebar from './sidebar';
import '../../styles/pages/dataManage.scss';
import actions from '../../store/modules/dataManage/actionCreator';

const { Sider, Content } = Layout;

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

class Container extends Component {
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        if (this.props.project && nextProps.project !== this.props.project) {
            // if(window.location.pathname !== '/data-manage/table') {
            //     this.props.router.push('/data-manage/table')
            // }
            // else {
            //     this.props.searchTable();
            // }
            // this.props.searchTable();

        }
    }

    render () {
        const { children } = this.props
        return (
            <Layout className="dt-dev-datamanagement g-datamanage">
                <Sider className="bg-w">
                    <Sidebar {...this.props} />
                </Sider>
                <Content style={{ position: 'relative' }}>
                    { children || '概览' }
                </Content>
            </Layout>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default connect(state => ({
    project: state.project.id
}), dispatch => ({
    searchTable (params) {
        dispatch(actions.searchTable(params));
    }
}))(Container);
