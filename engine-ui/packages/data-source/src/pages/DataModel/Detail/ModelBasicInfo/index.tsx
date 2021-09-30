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

import React from 'react';
import HTable from '../HTable';
import PaneTitle from '../../components/PaneTitle';
import DataInfo from '../DataInfo';
import RelationView from '../RelationView';
import { IModelDetail } from '@/pages/DataModel/types';
import { EnumSize } from '../types';

interface IPropsModelBasicInfo {
	modelDetail: Partial<IModelDetail>;
	size?: EnumSize;
}

const ModelBasicInfo = (props: IPropsModelBasicInfo) => {
	const { modelDetail, size = EnumSize.LARGE } = props;
	if (modelDetail === null) return null;
	return (
		<div className="inner-container">
			<div className="margin-bottom-20">
				<PaneTitle title="模型信息" />
				<HTable
					detail={{
						...modelDetail,
						dsName: `${modelDetail.dsName}(${modelDetail.dsTypeName})`,
					}}
					size={size}
				/>
			</div>
			{size === EnumSize.LARGE && (
				<div className="margin-bottom-20">
					<PaneTitle title="关联视图" />
					<RelationView modelDetail={modelDetail} />
				</div>
			)}
			<div className="margin-bottom-20">
				<PaneTitle title="数据信息" />
				<DataInfo
					relationTableList={modelDetail.joinList}
					metricList={modelDetail.columns.filter((item) => item.metric)}
					dimensionList={modelDetail.columns.filter((item) => item.dimension)}
					size={size}
				/>
			</div>
		</div>
	);
};

export default ModelBasicInfo;
