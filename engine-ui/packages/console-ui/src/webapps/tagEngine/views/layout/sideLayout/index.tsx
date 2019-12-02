import * as React from 'react';
import { Layout, Icon } from 'antd';
import { connect } from 'react-redux'
import * as ProjectAction from '../../../reducers/modules/project';
import * as UserAction from '../../../reducers/modules/user';
import { tagApp } from 'config/base';
import { updateApp, getLicenseApp } from 'main/actions/app'

import utils from 'utils'
import Heade from '../header';
import SideBar from '../sideBar';
import DashBoard from '../../dashboard/index';
import NavData from '../../../consts/navData';

const { Sider, Content } = Layout;

interface IProps {
    children: any;
    dispatch: any;
    location?: any;
    params: any;
    router: any;
}
interface IState {
    collapsed: boolean;
    mode: 'inline' | 'vertical';
}

class SideLayout extends React.Component<IProps, IState> {
    state: IState = {
        collapsed: false,
        mode: 'inline'
    };
    static propTypes: any;
    static defaultProps: any;
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(UserAction.getUser())
        dispatch(updateApp(tagApp))
        dispatch(getLicenseApp())
        dispatch(ProjectAction.getProjects())
        this.initProject()
    }
    initProject () {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            let pid = '';
            const projectIdFromURL = utils.getParameterByName('pid');
            const projectIdFromCookie = utils.getCookie('tag_project_id');
            if (projectIdFromURL) { // 优先从URL截取项目ID, 后从 Cookie 获取
                pid = projectIdFromURL;
            } else if (projectIdFromCookie) {
                pid = projectIdFromCookie;
            }
            if (pid) {
                dispatch(ProjectAction.getProject(parseInt(pid, 10)))
            }
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const nowId = nextProps.params.pid
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(ProjectAction.getProject(nowId))
        }
    }
    toggleCollapsed = () => {
        this.setState({
            collapsed: !this.state.collapsed,
            mode: !this.state.collapsed ? 'vertical' : 'inline'
        });
    }
    render () {
        const { children, location } = this.props;
        const pathName = location.pathname;
        const menuData = NavData.filter(item => item.routers.some(ele => new RegExp(ele, 'g').test(pathName)));
        const projectIdFromCookie = utils.getCookie('tag_project_id');
        return (
            <Layout className="dt-tag-layout">
                <Heade {...this.props} showMenu navData={NavData}/>
                <Layout className="tag-container">
                    {
                        children ? <React.Fragment>
                            <Sider
                                className="bg-w ant-slider-pos"
                                collapsed={this.state.collapsed}
                            >
                                <div
                                    className="ant-slider-pos--collapsed"
                                    onClick={this.toggleCollapsed}
                                >
                                    <Icon
                                        type={
                                            this.state.collapsed
                                                ? 'menu-unfold'
                                                : 'menu-fold'
                                        }
                                    />
                                </div>
                                <SideBar menuData={menuData[0] ? menuData[0].children : [] } {...this.props} mode={this.state.mode} />
                            </Sider>
                            <Content className="inner-container" key={projectIdFromCookie} style={{ paddingLeft: this.state.collapsed ? 84 : 220 }}>{children}</Content>
                        </React.Fragment> : (<DashBoard/>)
                    }

                </Layout>

            </Layout>
        );
    }
}

function mapStateToProps (state: any) {
    return {
        user: state.user,
        projects: state.projects,
        project: state.project,
        apps: state.apps,
        app: state.app
    }
}
export default connect(mapStateToProps)(SideLayout);
