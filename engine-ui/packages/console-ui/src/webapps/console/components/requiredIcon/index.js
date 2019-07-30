import React, { Component } from 'react';
import { COMPONENT_TYPE_VALUE } from '../../consts'
export default class RequiredIcon extends Component {
    constructor (props) {
        super(props);
        this.state = {}
    }
    render () {
        const { componentData, showRequireStatus } = this.props;
        const { componentTypeCode } = componentData;
        const { flinkShowRequired,
            hiveShowRequired,
            carbonShowRequired,
            sparkShowRequired,
            dtYarnShellShowRequired,
            learningShowRequired,
            hiveServerShowRequired,
            hdfsShowRequired,
            yarnShowRequired,
            libraShowRequired } = showRequireStatus;
        let isShowIcon = false;
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                isShowIcon = flinkShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                isShowIcon = hiveShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                isShowIcon = carbonShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                isShowIcon = sparkShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                isShowIcon = dtYarnShellShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                isShowIcon = learningShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                isShowIcon = hiveServerShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                isShowIcon = hdfsShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                isShowIcon = yarnShowRequired;
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                isShowIcon = libraShowRequired;
                break;
            }
            default: {
                return false
            }
        }
        return isShowIcon && <span className='icon_required'>*</span>
    }
}
