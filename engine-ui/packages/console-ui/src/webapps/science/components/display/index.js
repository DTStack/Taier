// import React from 'react';
// import { Circle } from 'widgets/circle';
// import { Icon, Tooltip } from 'antd';

import { COMPONENT_TYPE } from '../../consts';

export function nodeTypeIcon (type) {
    const imgBase = 'public/science/img/icon/';
    let imageName = '';
    switch (type) {
        case COMPONENT_TYPE.DATA_SOURCE: {
            return `<i class="anticon anticon-database"></i>`
        }
        case COMPONENT_TYPE.DATA_TOOLS: {
            return `<i class="anticon anticon-tool"></i>`
        }
        case COMPONENT_TYPE.DATA_MERGE: {
            imageName = 'data_merge'; break;
        }
        case COMPONENT_TYPE.DATA_PRE_HAND: {
            imageName = 'data_prehand'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING: {
            imageName = 'machine_learning'; break;
        }
        case COMPONENT_TYPE.DATA_PREDICT: {
            imageName = 'data_predict'; break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE: {
            imageName = 'data_evaluate'; break;
        }
        default: return '';
    }

    return `<img class="my-icon" alt="${type}" src="${imgBase}${imageName}.svg" />`
}

export function nodeStatus (status) {
    switch (status) {
        default: {
            return `<span></span>`
        }
    }
}
