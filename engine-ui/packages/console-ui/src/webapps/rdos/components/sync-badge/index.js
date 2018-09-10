import React from 'react';
import { Tooltip } from 'antd';

export default class SyncBadge extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { notSynced } = this.props;

        return notSynced ? <Tooltip
            title="修改已保存至本地存储但尚未同步到服务器，你可以点击上面的保存按钮立即同步。"
        >
           <span
            style={{
                display: 'inline-block',
                marginRight: 8,
                borderRadius: '50%',
                border: '4px solid ',
                borderColor: '#EF5350',
                position: 'relative',
                top: '-6px'
            }}
        ></span>
        </Tooltip> : <span
            style={{
                display: 'inline-block',
                marginRight: 8,
                borderRadius: '50%',
                border: '4px solid ',
                borderColor: '#00A755',
                position: 'relative',
                top: '-6px'
            }}
        ></span>;
    }
}