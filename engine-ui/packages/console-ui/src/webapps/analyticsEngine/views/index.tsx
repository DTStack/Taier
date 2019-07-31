import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import { aeApp } from 'config/base'

import Header from './layout/header'
import Workbench from '../views/workbench'

import { updateApp } from 'main/actions/app'

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

@(connect() as any)
class Main extends React.Component<any, any> {
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(updateApp(aeApp));
    }

    render () {
        const { children } = this.props
        return (
            <div className="app-analytics main header-fixed">
                <Header/>
                <div className="container">
                    { children || <Workbench /> }
                </div>
            </div>
        )
    }
}
Main.propTypes = propType
Main.defaultProps = defaultPro

export default Main
