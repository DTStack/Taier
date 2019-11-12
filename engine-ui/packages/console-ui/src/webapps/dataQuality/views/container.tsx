import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

// import utils from 'utils'
import { dqApp } from 'config/base'

import Header from './layout/header'
import ProjectPanel from './projectPanel'

// import { currentApp } from '../consts'
import * as UserAction from '../actions/user'
import { dataSourceActions } from '../actions/dataSource'
import { commonActions } from '../actions/common'
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
    componentDidMount () {
        const { dispatch } = this.props
        dispatch(UserAction.getUser());
        dispatch(updateApp(dqApp));
        dispatch(commonActions.getUserList());
        dispatch(commonActions.getAllDict());
        dispatch(dataSourceActions.getDataSourcesType());
    }

    render () {
        const { children } = this.props
        return (
            <div className="main header-fixed">
                <Header {...this.props}/>
                <div className="container">
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
