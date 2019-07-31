import * as React from 'react';
import { Tooltip } from 'antd';

class TableTipButton extends React.Component<any, any> {
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
        const src: any = {
            open: '/public/rdos/img/tip-table-active.svg',
            close: '/public/rdos/img/tip-table.svg',
            dark_close: '/public/rdos/img/theme-dark/tip-table.svg'
        }
        let url = src.close;

        if (theme != 'vs') {
            url = src.dark_close;
        }
        if (showTableTooltip) {
            url = src.open;
        }
        return (
            <Tooltip title="表信息帮助面板">
                <img
                    onClick={this.changeTableTipVisible.bind(this)}
                    className="pointer"
                    style={{ marginTop: '4px', height: '16px' }}
                    src={url}
                />
            </Tooltip>
        )
    }
}
export default TableTipButton;
