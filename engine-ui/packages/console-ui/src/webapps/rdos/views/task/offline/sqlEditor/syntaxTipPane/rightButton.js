import React from 'react';
import { Tooltip } from 'antd';

class RightButton extends React.Component {
    changeTableTipVisible () {
        const { showSyntaxPane, hideRightPane, showRightSyntaxPane } = this.props;
        if (showSyntaxPane) {
            hideRightPane();
        } else {
            showRightSyntaxPane();
        }
    }
    render () {
        const { theme, showSyntaxPane } = this.props;
        const src = {
            open: '/public/rdos/img/tip-syntax-active.svg',
            close: '/public/rdos/img/tip-syntax.svg',
            dark_close: '/public/rdos/img/theme-dark/tip-syntax.svg'
        }
        let url = src.close;

        if (theme != 'vs') {
            url = src.dark_close;
        }
        if (showSyntaxPane) {
            url = src.open;
        }
        return (
            <Tooltip title="SQL语法帮助面板">
                <img
                    onClick={this.changeTableTipVisible.bind(this)}
                    className="pointer"
                    style={{ marginTop: '4px', marginLeft: '5px', height: '16px' }}
                    src={url}
                />
            </Tooltip>
        )
    }
}
export default RightButton;
