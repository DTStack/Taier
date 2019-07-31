import * as React from 'react';
import { Tooltip } from 'antd';

class RightButton extends React.Component<any, any> {
    changeTableTipVisible () {
        const { showSyntaxPane, hideRightPane, showRightSyntaxPane } = this.props;
        if (showSyntaxPane) {
            hideRightPane();
        } else {
            showRightSyntaxPane();
        }
    }
    render () {
        const { theme, showSyntaxPane, notShowSyntax } = this.props;
        const src: any = {
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
                    style={{ display: notShowSyntax ? 'none' : 'inline-block', marginTop: '4px', marginLeft: '5px', height: '16px' }}
                    src={url}
                />
            </Tooltip>
        )
    }
}
export default RightButton;
