/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { omit } from 'lodash';

import CustomTreeSelect, { CustomTreeSelectProps } from './customTreeSelect';
import ajax from '../../api';

import { updateCatalogueData } from '../../controller/catalogue/actionCreator';

type DataType = 'task' | 'resource' | 'function';
interface FolderPickerProps extends CustomTreeSelectProps {
    dataType: DataType;
}

class FolderPicker extends Component<FolderPickerProps, any> {
    loadDataAsync = async (treeNode: any) => {
        const { updateTreeData } = this.props;
        const { catalogueType, id, children } = treeNode.props.dataRef;
        if (children?.length) return Promise.resolve();
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
            const { data } = res;
            updateTreeData(data);
        }
    };

    render() {
        const { treeData } = this.props;
        return (
            <>
                <CustomTreeSelect
                    {...omit(this.props, ['treeData', 'loadData'])}
                    showFile={this.props.showFile}
                    loadData={this.loadDataAsync}
                    treeData={treeData}
                />
            </>
        );
    }
}

const findCustomNode = (functionTree: any) => {
    const stack = [functionTree];
    let res;
    while (stack.length) {
        const node = stack.shift();
        if (node.name === '自定义函数') {
            res = node;
            break;
        }
        stack.push(...(node.children || []));
    }

    return res;
};

const mapState = (state: any, ownProps: FolderPickerProps) => {
    const { dataType } = ownProps;
    const { catalogue } = state;
    let treeData = null;
    switch (dataType) {
        case 'task':
            treeData = catalogue.taskTree;
            break;
        case 'resource':
            // resource manager NOT support to insert data into root folder
            treeData = catalogue.resourceTree.children?.find(
                (item: any) => item.catalogueType === 'ResourceManager'
            );
            break;
        case 'function':
            // function manager only support to insert data into custom function
            treeData = findCustomNode(catalogue.functionTree);
            break;
        default:
            treeData = catalogue.taskTree;
            break;
    }
    return {
        treeData,
    };
};

const mapDispatch = (dispatch: any, ownProps: FolderPickerProps) => {
    const { dataType } = ownProps;
    return {
        updateTreeData: (data: any) => {
            updateCatalogueData(dispatch, data, dataType);
        },
    };
};

export default connect(mapState, mapDispatch)(FolderPicker);
