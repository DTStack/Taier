import React from 'react';
import { connect } from 'react-redux'

import {
    workbenchAction
} from '../../../../../store/modules/offlineTask/actionType';

@connect(state => {
    return {
        showTableTooltip: state.offlineTask.workbench.showTableTooltip,
        theme: state.editor.options.theme
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
        const { theme, showTableTooltip } = this.props;
        const src = {
            open: '/public/rdos/img/tip-active.svg',
            close: '/public/rdos/img/tip.svg',
            dark_close: '/public/rdos/img/theme-dark/tip.svg'
        }
        let url = src.close;

        if (theme != 'vs') {
            url = src.dark_close;
        }
        if (showTableTooltip) {
            url = src.open;
        }
        return (
            <img
                onClick={this.changeTableTipVisible.bind(this)}
                className="pointer"
                style={{ marginTop: '4px', height: '20px' }}
                src={url}
            />
        )
    }
}
export default TableTipButton;
