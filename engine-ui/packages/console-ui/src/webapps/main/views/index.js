import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import utils from 'utils'
import NotFund from 'widgets/notFund'

import { initConfig } from 'funcs';
import { getInitUser } from '../actions/user'
import userActions from '../consts/userActions'
import http from '../api';

const propType = {
    children: PropTypes.node
}
const defaultPro = {
    children: []
}

@connect(state => {
    return {
        user: state.user
    }
})
class Main extends Component {
    componentDidMount () {
        const { user } = this.props;
        const userAction = getInitUser()

        this.props.dispatch(userAction);
        this.checkRoot(user);
    }
    componentWillReceiveProps (nextProps) {
        const { user } = nextProps;

        if (this.props.user.dtuicUserId != user.dtuicUserId && user.dtuicUserId) {
            this.checkRoot(user);
        }
    }
    checkRoot (user) {
        if (user && user.dtuicUserId) {
            http.checkRoot({ userId: user.dtuicUserId })
                .then(
                    (res) => {
                        if (res.code == 1) {
                            this.props.dispatch({
                                type: userActions.UPDATE_USER,
                                data: {
                                    isRoot: true
                                }
                            })
                        } else {
                            this.props.dispatch({
                                type: userActions.UPDATE_USER,
                                data: {
                                    isRoot: false
                                }
                            })
                        }
                    }
                )
                .catch(
                    (e) => {
                        console.log('控制台权限')
                    }
                );
        }
    }
    render () {
        return this.props.children || <NotFund />
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
