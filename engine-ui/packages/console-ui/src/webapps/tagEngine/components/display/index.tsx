import * as React from 'react';

import { IGroupType } from '../../model/group';

export function displayGroupType(type: IGroupType) {
    switch (type) {
        case IGroupType.REGULAR: {
            return <span>规则创建</span>
        }
        case IGroupType.UPLOAD: {
            return <span>手动上传</span>
        }
        default: {
            return ''
        }
    }
}