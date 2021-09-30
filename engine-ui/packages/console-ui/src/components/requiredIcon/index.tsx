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

import * as React from 'react';
import { COMPONENT_TYPE_VALUE } from '../../consts';
export default class RequiredIcon extends React.Component<any, any> {
	constructor(props: any) {
		super(props);
		this.state = {};
	}
	render() {
		const { componentData, showRequireStatus } = this.props;
		const { componentTypeCode } = componentData;
		const {
			flinkShowRequired,
			hiveShowRequired,
			carbonShowRequired,
			sparkShowRequired,
			dtYarnShellShowRequired,
			learningShowRequired,
			hiveServerShowRequired,
			hdfsShowRequired,
			yarnShowRequired,
			libraShowRequired,
			impalaSqlRequired,
			sftpShowRequired,
		} = showRequireStatus;
		let isShowIcon = false;
		switch (componentTypeCode) {
			case COMPONENT_TYPE_VALUE.FLINK: {
				isShowIcon = flinkShowRequired;
				break;
			}
			case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER: {
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
			case COMPONENT_TYPE_VALUE.HIVE_SERVER: {
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
			case COMPONENT_TYPE_VALUE.LIBRA_SQL: {
				isShowIcon = libraShowRequired;
				break;
			}
			case COMPONENT_TYPE_VALUE.IMPALA_SQL: {
				isShowIcon = impalaSqlRequired;
				break;
			}
			case COMPONENT_TYPE_VALUE.SFTP: {
				isShowIcon = sftpShowRequired;
				break;
			}
			default: {
				return false;
			}
		}
		return isShowIcon && <span className="icon_required">*</span>;
	}
}
