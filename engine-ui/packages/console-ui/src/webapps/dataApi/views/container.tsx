import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import Header from './layout/header'
import GlobalLoading from './layout/loading'
import { daApp } from 'config/base'
import { updateApp } from 'main/actions/app'
import ProjectPanel from './projectPanel'
import { commonActions } from '../actions/common'
import * as projectActions from '../actions/project'
import * as UserAction from '../actions/user'

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
    static propTypes: any;
    static defaultProps: any;
    coverConatinerBg: boolean = false;
    /* eslint-disable-next-line */
    componentWillMount () {
        const { dispatch } = this.props;
        dispatch(commonActions.getMenuList())
    }
    componentDidMount () {
        const { dispatch } = this.props
        dispatch(UserAction.getUser())
        dispatch(projectActions.getProjects())
        dispatch(projectActions.getAllProjects())

        dispatch(updateApp(daApp))
        this.initProject()
    }

    initProject () {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            let pid = '';
            const projectIdFromURL = utils.getParameterByName('pid');
            const projectIdFromCookie = utils.getCookie('api_project_id');
            if (projectIdFromURL) { // 优先从URL截取项目ID, 后从 Cookie 获取
                pid = projectIdFromURL;
            } else if (projectIdFromCookie) {
                pid = projectIdFromCookie;
            }
            if (pid) {
                dispatch(projectActions.getProject(parseInt(pid, 10)))
            }
        }
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const nowId = nextProps.params.pid;
        const menuList = nextProps.common.menuList;
        const pathname = nextProps.router.location.pathname;
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(projectActions.getProject(nowId));
            if (this.props.common.menuList != menuList && menuList) {
                const routerArr = this.getPermissionRouter(menuList);
                // 切换项目无权限，则跳转
                if (routerArr.indexOf(pathname.split('/')[2]) == -1 && pathname != '/') {
                    nextProps.router.push('/');
                }
            }
        }
        if (pathname == '/') { // 首页背景填充
            this.coverConatinerBg = true;
        } else {
            this.coverConatinerBg = false;
        }
    }
    /**
     * 获取有权限的路由
     */
    getPermissionRouter = (menuList: string[] = []) => {
        const routerArr: string[] = [];
        for (let item of menuList) {
            switch (item) {
                case 'overview_market_menu': {
                    routerArr.push('overview');
                    break;
                }
                case 'api_market_menu': {
                    routerArr.push('market');
                    break;
                }
                case 'api_myapi_menu': {
                    routerArr.push('mine');
                    break;
                }
                case 'api_manager_menu': {
                    routerArr.push('manage');
                    break;
                }
                case 'api_datasource_menu': {
                    routerArr.push('dataSource');
                    break;
                }
                case 'api_authorized_menu': {
                    routerArr.push('approvalAndsecurity');
                    break;
                }
                case 'project_manager_menu': {
                    routerArr.push('project');
                    break;
                }
            }
        }
        return routerArr;
    }

    render () {
        let { children } = this.props;
        let header = <Header {...this.props} />
        if (!this.props.common.menuList) {
            children = <GlobalLoading />;
            header = null;
        }
        const style: any = {
            background: this.coverConatinerBg ? '#0C0F26' : '#f2f7fa'
        }
        return (
            <div className="dt-dev-tools" id="JS_APP">
                {header}
                <div className="container" style={style}>
                    { children || <ProjectPanel /> }
                </div>
            </div>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro

function mapStateToProps (state: any) {
    return {
        user: state.user,
        projects: state.projects,
        project: state.project,
        apps: state.apps,
        app: state.app,
        common: state.common
    }
}
export default connect(mapStateToProps)(Container)
