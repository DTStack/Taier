import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import { dqApp } from 'config/base'

import Header from './layout/header'
import ProjectPanel from './projectPanel'

// import { currentApp } from '../consts'
import * as UserAction from '../actions/user'
import { dataSourceActions } from '../actions/dataSource'
import { commonActions } from '../actions/common'
import * as projectActions from '../actions/project'
import { updateApp } from 'main/actions/app'

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Main extends React.Component<any, any> {
    static propTypes = propType
    static defaultProps = defaultPro

    coverConatinerBg: boolean = false;

    componentDidMount () {
        const { dispatch } = this.props
        dispatch(UserAction.getUser());
        dispatch(updateApp(dqApp));
        dispatch(commonActions.getUserList());
        dispatch(commonActions.getAllDict());
        dispatch(dataSourceActions.getDataSourcesType());
        dispatch(projectActions.getProjects())
        dispatch(projectActions.getAllProjects())
        this.initProject();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const nowId = nextProps.params.pid
        if (nowId && nowId !== this.props.params.pid) {
            this.props.dispatch(projectActions.getProject(nowId))
        }
        if (nextProps.location.pathname == '/') { // 首页背景填充
            this.coverConatinerBg = true;
        } else {
            this.coverConatinerBg = false;
        }
    }

    initProject () {
        const { dispatch, router } = this.props
        const pathname = router.location.pathname
        if (pathname !== '/') {
            let pid = '';
            const projectIdFromURL = utils.getParameterByName('pid');
            const projectIdFromCookie = utils.getCookie('dq_project_id');
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

    render () {
        const { children } = this.props;
        const style: any = {
            background: this.coverConatinerBg ? '#0C0F26' : '#f2f7fa'
        };
        return (
            <div className="main header-fixed">
                <Header {...this.props}/>
                <div className="container" style={style}>
                    { children || <ProjectPanel /> }
                </div>
            </div>
        )
    }
}

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
export default connect(mapStateToProps)(Main)
