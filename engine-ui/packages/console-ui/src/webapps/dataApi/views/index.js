import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'

import Header from './layout/header'
import GlobalLoading from './layout/loading'
import * as UserAction from '../actions/user'
import { commonActions } from '../actions/common'

import { updateApp } from 'main/actions/app'

import { daApp } from 'config/base'

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}
const mapStateToProps = state => {
    const { common } = state;
    return { common }
};
@connect(
    mapStateToProps
)
class Main extends Component {
    // eslint-disable-next-line
	componentWillMount () {
        const { dispatch } = this.props;
        dispatch(commonActions.getMenuList())
    }
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(UserAction.getUser())
        dispatch(updateApp(daApp));
    }

    render () {
        let { children } = this.props
        let header = <Header />
        if (!this.props.common.menuList) {
            children = <GlobalLoading />;
            header = null;
        }
        return (
            <div className="main">

                {header}
                <div className="container overflow-x-hidden">
                    { children || '加载中....' }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
