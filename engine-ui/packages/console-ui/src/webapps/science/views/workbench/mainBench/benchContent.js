import React, { Component } from 'react';
import { connect } from 'react-redux';

import { siderBarType } from '../../../consts'

import PanelGroup from './panelGroup'
import ModelView from './model'
@connect((state) => {
    return {
        siderBarKey: state.common.siderBarKey
    }
})
class BenchContent extends Component {
    renderContent () {
        const { siderBarKey } = this.props;
        switch (siderBarKey) {
            case siderBarType.notebook: {
                return <PanelGroup />
            }
            case siderBarType.experiment: {
                return <PanelGroup />
            }
            case siderBarType.component: {
                return <PanelGroup />
            }
            case siderBarType.model: {
                return <ModelView />
            }
        }
    }

    render () {
        return (
            <div className="m-content">
                {this.renderContent()}
            </div>
        )
    }
}

export default BenchContent
