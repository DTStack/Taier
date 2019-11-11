import * as React from 'react';
import { Tooltip } from 'antd';
import './style.scss';

export default (props: any) => {
    let { value = '', className = '' } = props;
    return (
        <Tooltip title={`${value}`}>
            <div className={'ellipsisText' + className}>
                {`${value}`}
            </div>
        </Tooltip>

    )
}
