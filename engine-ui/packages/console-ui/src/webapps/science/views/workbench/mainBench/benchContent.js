import React, { Component } from 'react';
import { connect } from 'react-redux';

import { siderBarType } from '../../../consts'

import NoteBookGroup from './panelGroup/notebookGroup';
import GraphGroup from './panelGroup/graphGroup';
import ModelView from './model'
@connect((state) => {
    return {
        siderBarKey: state.common.siderBarKey,
        experiment: state.experiment,
        component: state.component,
        notebook: state.notebook
    }
})
class BenchContent extends Component {
    renderContent () {
        const { siderBarKey } = this.props;
        switch (siderBarKey) {
            case siderBarType.notebook: {
                return <NoteBookGroup />
            }
            case siderBarType.experiment: {
                return <GraphGroup />
            }
            case siderBarType.component: {
                return <GraphGroup />
            }
            case siderBarType.model: {
                return <ModelView />
            }
        }
    }

    render () {
        return this.renderContent()
    }
}

export default BenchContent
