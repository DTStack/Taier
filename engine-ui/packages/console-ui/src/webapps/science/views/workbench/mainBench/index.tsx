import * as React from 'react';
import { connect } from 'react-redux';
import BenchContent from './benchContent';
import { bindActionCreators } from 'redux';

import * as runTaskActions from '../../../actions/runTaskActions';

@(connect((state: any) => {
    return {
        running: state.editor.running
    }
}, (dispatch: any) => {
    return {
        ...bindActionCreators(runTaskActions, dispatch)
    }
}) as any)
class MainBench extends React.Component<any, any> {
    componentWillUnmount () {
        const { running } = this.props;
        if (running && running.length) {
            running.forEach((tabId: any) => {
                this.props.stopTask(tabId, true);
            })
        }
    }
    render () {
        return (
            <BenchContent />
        )
    }
}
export default MainBench;
