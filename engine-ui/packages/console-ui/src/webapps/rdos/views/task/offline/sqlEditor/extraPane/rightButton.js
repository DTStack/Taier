import React from 'react';
import { connect } from 'react-redux'

import {
    workbenchAction
} from '../../../../../store/modules/offlineTask/actionType';

@connect(state => {
    return {
        showTableTooltip: state.offlineTask.workbench.showTableTooltip
    }
})
class TableTipButton extends React.Component {
    changeTableTipVisible () {
        const { showTableTooltip, dispatch } = this.props;
        dispatch({
            type: showTableTooltip ? workbenchAction.CLOSE_TABLE_TOOLTIP : workbenchAction.OPEN_TABLE_TOOLTIP
        })
    }
    render () {
        return (
            <img onClick={this.changeTableTipVisible.bind(this)} className="pointer" style={{ marginTop: '4px', height: '20px' }} src="/public/rdos/img/tip.svg" />
        )
    }
}
export default TableTipButton;
