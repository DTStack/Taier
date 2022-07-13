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

import { useEffect, useMemo, useState } from 'react';
import { omit } from 'lodash';
import type { CustomTreeSelectProps } from './customTreeSelect';
import CustomTreeSelect from './customTreeSelect';
import { CATELOGUE_TYPE, MENU_TYPE_ENUM } from '@/constant';
import molecule from '@dtinsight/molecule';
import resourceManagerTree from '@/services/resourceManagerService';
import functionManagerService from '@/services/functionManagerService';
import type { TreeSelectProps } from 'antd/lib/tree-select';
import { catalogueService } from '@/services';
import api from '@/api';
import { getTenantId } from '@/utils';

interface FolderPickerProps extends CustomTreeSelectProps {
	dataType: CATELOGUE_TYPE;
}

export default function FolderPicker(props: FolderPickerProps) {
	const [flag, rerender] = useState(false);
	const [loading, setLoading] = useState(false);

	const loadDataAsync: TreeSelectProps['loadData'] = async (treeNode) => {
		const currentData = treeNode.props.dataRef;
		if (!currentData.children?.length) {
			await catalogueService.loadTreeNode(
				{ id: currentData?.data?.id, catalogueType: currentData?.data?.catalogueType },
				props.dataType,
			);
			rerender((f) => !f);
		}
	};

	const treeData = useMemo(() => {
		switch (props.dataType) {
			case CATELOGUE_TYPE.TASK:
				return (molecule.folderTree.getState().folderTree?.data || [])[0];
			case CATELOGUE_TYPE.RESOURCE: {
				// resource manager NOT support to insert data into root folder
				const resourceData = (resourceManagerTree.getState().folderTree?.data || [])[0];
				return resourceData?.children?.find(
					(item) => item.data.catalogueType === MENU_TYPE_ENUM.RESOURCE,
				);
			}
			case CATELOGUE_TYPE.FUNCTION: {
				// function manager only support to insert data into custom function
				return (functionManagerService.getState().folderTree?.data || [])[0];
			}
			default:
				return undefined;
		}
	}, [props.dataType, flag]);

	useEffect(() => {
		switch (props.dataType) {
			case CATELOGUE_TYPE.TASK:
				break;
			case CATELOGUE_TYPE.RESOURCE: {
				if (
					props.value !== undefined &&
					props.value !== null &&
					!resourceManagerTree.get(`${props.value}-folder`)
				) {
					setLoading(true);
					api.getResourceLocation<number[]>({
						tenantId: getTenantId(),
						catalogueType: MENU_TYPE_ENUM.RESOURCE,
						id: props.value,
					}).then((res) => {
						if (res.code === 1) {
							const idCollection = res.data.reverse();
							Promise.all([
								...idCollection.map((id) => {
									if (
										!resourceManagerTree.get(`${id}-folder`)?.children?.length
									) {
										return catalogueService.loadTreeNode(
											{
												id,
												catalogueType: MENU_TYPE_ENUM.RESOURCE,
											},
											CATELOGUE_TYPE.RESOURCE,
										);
									}

									return Promise.resolve();
								}),
							]).finally(() => {
								setLoading(false);
								rerender((f) => !f);
							});
						}
					});
				}
				break;
			}
			case CATELOGUE_TYPE.FUNCTION:
				break;
			default:
				break;
		}
	}, [props.value]);

	return (
		<>
			<CustomTreeSelect
				loading={loading}
				{...omit(props, ['treeData', 'loadData'])}
				dataType={props.dataType}
				showFile={props.showFile}
				loadData={loadDataAsync}
				treeData={treeData}
			/>
		</>
	);
}
