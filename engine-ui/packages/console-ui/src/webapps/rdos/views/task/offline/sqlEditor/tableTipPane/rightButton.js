import React from 'react';

class TableTipButton extends React.Component {
    changeTableTipVisible () {
        const { showTableTooltip, hideRightPane, showRightTablePane } = this.props;
        if (showTableTooltip) {
            hideRightPane();
        } else {
            showRightTablePane();
        }
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
