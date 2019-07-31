import * as React from 'react';
import { connect } from 'react-redux';

import { siderBarType } from '../../../consts'

import NoteBookGroup from './panelGroup/notebookGroup';
import GraphGroup from './panelGroup/graphGroup';
import ModelView from './model'
@(connect((state: any) as any) => {
    return {
        siderBarKey: state.common.siderBarKey,
        experiment: state.experiment,
        component: state.component,
        notebook: state.notebook
    }
})
class BenchContent extends React.Component<any, any> {
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
