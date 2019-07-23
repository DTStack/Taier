import React from 'react';
import { connect } from 'react-redux';
import BenchContent from './benchContent';
import { bindActionCreators } from 'redux';

import * as runTaskActions from '../../../actions/runTaskActions';

@connect(state => {
    return {
        running: state.editor.running
    }
}, dispatch => {
    return {
        ...bindActionCreators(runTaskActions, dispatch)
    }
})
class MainBench extends React.Component {
    componentWillUnmount () {
        const { running } = this.props;
        if (running && running.length) {
            running.forEach((tabId) => {
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
