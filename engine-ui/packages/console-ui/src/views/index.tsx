import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import Header from './layout/header'

import { updateApp } from 'dt-common/src/actions/app'
import { consoleApp } from 'dt-common/src/consts/defaultApps'

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}
const mapStateToProps = (state: any) => {
    const { common } = state;
    return { common }
};
@(connect(
    mapStateToProps
) as any)
/* eslint-disable */
class Main extends React.Component<any, any> {
    static defaultProps = defaultPro;
    static propTypes = propType;
    componentWillMount () {
        const { dispatch } = this.props;
    }
    componentDidMount () {
        const { dispatch } = this.props;
        dispatch(updateApp(consoleApp));
    }

    render () {
        let { children } = this.props
        let header = <Header />
        return (
            <div className="main">

                {header}
                <div className="container overflow-x-hidden" id='JS_console_container'>
                    { children || '加载中....' }
                </div>
            </div>
        )
    }
}
// Main.propTypes = propType
// Main.defaultProps = defaultPro

export default Main
