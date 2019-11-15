import * as React from 'react'
import { Badge } from 'antd'

import { GROUP_STATUS } from '../../consts';

export function GroupStatus (props: any) {
    const value = props.value
    switch (value) {
        case GROUP_STATUS.FAIL:
            return <span><Badge status="error" text="失败"/></span>
        default:
            return <span><Badge status="processing" text="正常运行"/></span>
    }
}
