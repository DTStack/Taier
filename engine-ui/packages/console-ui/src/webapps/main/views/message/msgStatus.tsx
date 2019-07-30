import * as React from 'react'
import { Circle } from 'widgets/circle'

export default function msgStatus (props: any) {
    const { value } = props;
    let color = '';

    switch (value) {
        case 0: // 未读
            color = '#EF5350'; break;
        case 1: // 已读
        default:
            color = '#d9d9d9';
            break;
    }

    return <Circle style={{ background: color }} />
}
