import * as React from 'react';
import { Tooltip } from 'antd';

export default class SyncBadge extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        const { notSynced } = this.props;

        return notSynced ? <Tooltip
            title="修改已保存至本地存储但尚未同步到服务器，你可以点击上面的保存按钮立即同步。"
        >
            <span className="not-synced"></span>
        </Tooltip> : null;
    }
}
