import * as React from 'react'
import PropTypes from 'prop-types'
import { Layout, Icon } from 'antd'
import { connect } from 'react-redux';

import Sidebar from './sidebar';
import '../../styles/pages/dataManage.scss';
import actions from '../../store/modules/dataManage/actionCreator';

const { Sider, Content } = Layout;

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
    static propTypes = propType
    static defaultProps = defaultPro
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
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

    state: any = {
        collapsed: false,
        mode: 'inline'
    };
    toggleCollapsed = () => {
        this.setState({
            collapsed: !this.state.collapsed,
            mode: !this.state.collapsed ? 'vertical' : 'inline'
        });
    }
    render () {
        const { children } = this.props
        return (
            <Layout className="dt-dev-datamanagement g-datamanage">
                <Sider className="bg-w ant-slider-pos"
                    collapsed={this.state.collapsed}
                >
                    <div className="ant-slider-pos--collapsed" onClick={ this.toggleCollapsed }>
                        <Icon type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'} />
                    </div>
                    <Sidebar {...this.props} mode={this.state.mode} />
                </Sider>
                <Content style={{ position: 'relative' }}>
                    { children || '概览' }
                </Content>
            </Layout>
        )
    }
}
export default connect((state: any) => ({
    project: state.project.id
}), (dispatch: any) => ({
    searchTable (params: any) {
        dispatch(actions.searchTable(params));
    }
}))(Container);
