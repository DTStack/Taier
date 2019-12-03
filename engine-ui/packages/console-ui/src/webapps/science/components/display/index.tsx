// import * as React from 'react';
// import { Circle } from 'widgets/circle';
// import { Icon } from 'antd';

import { COMPONENT_TYPE } from '../../consts';

export function nodeTypeIcon (type: any) {
    const imgBase = 'public/science/img/icon/';
    let imageName = '';
    switch (type) {
        case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
        case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE: {
            return `<i class="anticon anticon-database"></i>`
        }
        case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT: {
            return `<i class="anticon anticon-tool"></i>`
        }
        case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE: // 类型转换|归一化
        case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
        {
            imageName = 'data_merge'; break;
        }
        case COMPONENT_TYPE.DATA_MERGE.STANDARD: // 标准化
        {
            imageName = 'standard'; break;
        }
        case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE:// 缺失值填充
        {
            imageName = 'miss-val'; break;
        }
        case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT: {
            imageName = 'data_prehand'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION: {
            imageName = 'machine_learning'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION: {
            imageName = 'kmeans'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION: {
            imageName = 'gbdt'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS: {
            imageName = 'gbdt'; break;
        }
        case COMPONENT_TYPE.MACHINE_LEARNING.SVM: {
            imageName = 'svm'; break;
        }
        case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT: {
            imageName = 'data_predict'; break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION: {
            imageName = 'data_evaluate'; break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION: {
            imageName = 'unionModel'; break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION: {
            imageName = 'regressionClassificcation'; break;
        }
        case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX: {
            imageName = 'confusion-matrix'; break;
        }
        case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT: {
            imageName = 'one-hot'; break;
        }
        case COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT: {
            imageName = 'python';
            break;
        }
        default: return '';
    }

    return `<img class="my-icon" alt="${type}" src="${imgBase}${imageName}.svg" />`
}

export function nodeStatus (status: any) {
    switch (status) {
        case 0: {
            // 中间态
            return (
                `<i class="anticon" style="float:right;margin-top:6px;"><span class="circle"></span><span class="circle"></span><span class="circle"></span></i>`
            )
        }
        case 1: {
            // 成功
            return `<i style="float:right;line-height:18px;color:rgb(21,167,74);" class="anticon anticon-check-circle"></i>`;
        }
        case 2: {
            // 失败
            return `<i style="float:right;line-height:18px;color:rgb(223,54,43);" class="anticon anticon-close-circle"></i>`;
        }
        default: {
            return `<span></span>`
        }
    }
}
