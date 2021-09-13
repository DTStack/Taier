import React, { Component } from 'react'
import { connect } from 'react-redux'
import { omit } from 'lodash'

import CustomTreeSelect ,{ CustomTreeSelectProps } from './customTreeSelect'
import ajax from '../../api';
import { 
    taskTreeAction,
    resTreeAction,
    sparkSysFnTreeActon,
    sparkCustomFnTreeAction 
} from '../../controller/catalogue/actionTypes'

type DataType = 'task'|'resource'|'sparkSysFunction'|'sparkCustomFunction'
interface FolderPickerProps extends CustomTreeSelectProps {
    dataType: DataType;
}

class FolderPicker extends Component<FolderPickerProps, any> {

    loadDataAsync = async (treeNode: any) => {
        const { updateTreeData } = this.props
        const { catalogueType, id, children } = treeNode.props.dataRef
        if (children?.length) return Promise.resolve()
        const res = await ajax.getOfflineCatalogue({
            isGetFile: !!1,
            nodePid: id,
            catalogueType,
            taskType: 1,
            appointProjectId: 1,
            projectId: 1,
            userId: 1,
        });
        if (res.code === 1) {
            const { data } = res
            updateTreeData(data)
        }
    }

    render () {
        const { treeData } = this.props
        return (
            <>
                <CustomTreeSelect
                    {...omit(this.props, ['treeData', 'loadData'])}
                    showFile={this.props.showFile}
                    loadData={this.loadDataAsync}
                    treeData={treeData}
                />
            </>
        )
    }
}

const mapState = (state: any, ownProps: FolderPickerProps) => {
    const { dataType } = ownProps
    const { catalogue } = state 
    let treeData = null
    switch (dataType) {
        case 'task': 
            treeData = catalogue.taskTree;
            break;
        case 'resource': 
            treeData = catalogue.resourceTree;
            break;
        case 'sparkCustomFunction':
            treeData = catalogue.sparkCustomFuncTree;
            break;
        case 'sparkSysFunction': 
            treeData = catalogue.sparkSystemFuncTreeData;
            break;
        default: 
            treeData = catalogue.taskTree;
            break;
    }
    return {
        treeData
    };
}

const mapDispatch = (dispatch: any, ownProps: FolderPickerProps) => {
    const { dataType } = ownProps
    let action: any = null
    switch (dataType) {
        case 'task': 
            action = taskTreeAction
            break;
        case 'resource': 
            action = resTreeAction;
            break;
        case 'sparkCustomFunction':
            action = sparkCustomFnTreeAction;
            break;
        case 'sparkSysFunction': 
            action = sparkSysFnTreeActon;
            break;
        default: 
            action = taskTreeAction;
            break;
    }
    return {
        updateTreeData: (data: any) => {
            dispatch({
                type: action.LOAD_FOLDER_CONTENT,
                payload: data
            }) 
        }
    }
} 

export default connect(mapState, mapDispatch)(FolderPicker)


