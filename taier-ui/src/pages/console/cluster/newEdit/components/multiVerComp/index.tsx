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

import { useMemo, useState, useEffect } from 'react';
import { Tabs, Menu, Dropdown, Button, Radio, Row, Col } from 'antd';
import type { MenuInfo } from 'rc-menu/lib/interface';
import { DownOutlined, CaretRightOutlined } from '@ant-design/icons';
import TestRestIcon from '@/components/testResultIcon';
import { isFLink } from '../../help';
import ToolBar from '../toolbar';
import SingleVerComp from '../singleVerComp';
import {
	COMPONENT_CONFIG_NAME,
	COMP_ACTION,
	FLINK_DEPLOY_TYPE,
	FLINK_DEPLOY_NAME,
} from '@/constant';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import type {
	IClusterInfo,
	IScheduleComponentComp,
	ISaveCompsData,
	ITestStatusProps,
	IGetLoadTemplateParams,
	IVersionData,
	ISaveComp,
	IHandleConfirm,
	ITestConnects,
} from '../../interface';
import './index.scss';

const { TabPane } = Tabs;
const MenuItem = Menu.Item;
interface IProps {
	comp: IScheduleComponentComp;
	view: boolean;
	saveCompsData: ISaveCompsData[];
	versionData: IVersionData;
	clusterInfo: IClusterInfo;
	testStatus: ITestStatusProps[];
	saveComp: ISaveComp;
	isCheckBoxs: boolean;
	handleConfirm: IHandleConfirm;
	testConnects: ITestConnects;
	getLoadTemplate: (key?: string | number, params?: IGetLoadTemplateParams) => void;
	onVersionChange?: (versionName: string, typeCode: COMPONENT_TYPE_VALUE) => void;
}

const className = 'c-multiVersionComp';

export default function MultiVersionComp({
	comp,
	view,
	saveCompsData,
	versionData,
	clusterInfo,
	isCheckBoxs,
	testStatus,
	saveComp,
	getLoadTemplate,
	handleConfirm,
	testConnects,
	onVersionChange,
}: IProps) {
	const [deployType, setDeployType] = useState<keyof typeof FLINK_DEPLOY_NAME>(
		comp?.multiVersion[0]?.deployType ?? FLINK_DEPLOY_TYPE.YARN,
	);
	const [currVersion, setCurrVersion] = useState(comp?.multiVersion?.[0]?.versionName);

	useEffect(() => {
		onVersionChange?.(currVersion!, comp?.componentTypeCode);
	}, [currVersion, comp?.componentTypeCode]);

	const handleMenuClick = (e: MenuInfo) => {
		const typeCode = comp?.componentTypeCode ?? '';
		saveComp(
			{
				componentTypeCode: typeCode,
				versionName: e.key,
				deployType,
				isDefault: false,
			},
			COMP_ACTION.ADD,
		);
		getLoadTemplate(typeCode, { compVersion: e.key, deployType });
	};

	const getMeunItem = (displayVersion: IVersionData[string]) => {
		const typeCode: keyof typeof COMPONENT_CONFIG_NAME = comp?.componentTypeCode ?? '';
		return (
			<Menu onClick={handleMenuClick}>
				{displayVersion?.map(({ key }) => {
					const disabled = comp?.multiVersion?.findIndex(
						(vcomp) => vcomp?.versionName === key,
					);
					return (
						<MenuItem disabled={disabled > -1} key={key}>
							{isFLink(typeCode)
								? FLINK_DEPLOY_NAME[deployType]
								: COMPONENT_CONFIG_NAME[typeCode]}{' '}
							{key}
						</MenuItem>
					);
				})}
			</Menu>
		);
	};

	const addMultiVersionComp = (key: string) => {
		const typeCode = comp?.componentTypeCode ?? '';
		saveComp(
			{
				deployType,
				componentTypeCode: typeCode,
				versionName: key,
				isDefault: true,
			},
			COMP_ACTION.ADD,
		);
		getLoadTemplate(typeCode, { compVersion: key, deployType });

		if (!currVersion) {
			setCurrVersion(key);
		}
	};

	const getComponentName = (
		typeCode: keyof typeof COMPONENT_CONFIG_NAME,
		type: keyof typeof FLINK_DEPLOY_NAME = FLINK_DEPLOY_TYPE.YARN,
	) => {
		if (isFLink(typeCode)) return FLINK_DEPLOY_NAME[type];
		return COMPONENT_CONFIG_NAME[typeCode];
	};

	const getDefaultVerionCompStatus = (component: IScheduleComponentComp) => {
		/** 当flink组件只有一个组件版本时勾选为默认版本 */
		const typeCode = component?.componentTypeCode ?? '';
		if (isFLink(typeCode) && component.multiVersion.length === 1) return true;
		return false;
	};

	const typeCode: keyof typeof COMPONENT_CONFIG_NAME = comp?.componentTypeCode ?? '';
	const isDefault = useMemo(() => getDefaultVerionCompStatus(comp), [comp]);

	// flink 由后端 deployTypes 字段控制展示版本
	const displayVersion = useMemo(() => {
		let tempVersion = versionData[COMPONENT_CONFIG_NAME[typeCode]] || [];
		if (isFLink(typeCode)) {
			tempVersion = tempVersion.filter((item) => item?.deployTypes?.includes(deployType));
		}
		return tempVersion;
	}, [typeCode]);

	if (!comp?.multiVersion[0]?.versionName) {
		return (
			<div className={className}>
				<div className={`${className}__intail`}>
					{isFLink(typeCode) && (
						<Row className={`${className}__intail__row`}>
							<Col span={10}>部署模式：</Col>
							<Col>
								<Radio.Group
									value={deployType}
									onChange={(e) => setDeployType(e.target.value)}
								>
									<Radio value={FLINK_DEPLOY_TYPE.YARN}>
										{FLINK_DEPLOY_NAME[FLINK_DEPLOY_TYPE.YARN]}
									</Radio>
								</Radio.Group>
							</Col>
						</Row>
					)}
					<Row className={`${className}__intail__row`}>
						<Col span={10}>选择版本：</Col>
						<Col style={{ display: 'flex' }}>
							{displayVersion?.map(({ key }) => {
								return (
									<div
										key={key}
										className={`${className}__intail__container__desc`}
										onClick={() => addMultiVersionComp(key)}
									>
										<span className="comp-name">
											<img
												src={`images/${COMPONENT_CONFIG_NAME[typeCode]}.png`}
											/>
											<span>
												{!isFLink(typeCode) &&
													`${COMPONENT_CONFIG_NAME[typeCode]} `}
												{key}
											</span>
										</span>
										<CaretRightOutlined />
									</div>
								);
							})}
						</Col>
					</Row>
				</div>
				{!view && (
					<ToolBar
						mulitple={false}
						comp={comp}
						clusterInfo={clusterInfo}
						saveComp={saveComp}
						handleConfirm={handleConfirm}
					/>
				)}
			</div>
		);
	}

	return (
		<div className={className}>
			<Tabs
				tabPosition="top"
				activeKey={currVersion}
				className={`${className}__tabs`}
				onChange={(activeKey) => setCurrVersion(activeKey)}
				tabBarExtraContent={
					<Dropdown
						disabled={view}
						overlay={() => getMeunItem(displayVersion)}
						placement="bottom"
					>
						<Button type="primary" size="small" style={{ marginRight: 20 }}>
							添加版本
							<DownOutlined />
						</Button>
					</Dropdown>
				}
			>
				{comp?.multiVersion.map((vcomp) => {
					const { deployType: type, componentTypeCode, versionName } = vcomp!;
					const currStatus = testStatus.find(
						(status) => status?.componentVersion === versionName,
					);
					return (
						<TabPane
							tab={
								<span>
									<span style={{ marginRight: 6 }}>
										{getComponentName(componentTypeCode, type)}
										{versionName}
									</span>
									<TestRestIcon testStatus={currStatus!} />
								</span>
							}
							key={String(versionName)}
						>
							<SingleVerComp
								comp={vcomp!}
								view={view}
								isDefault={isDefault}
								isCheckBoxs={isCheckBoxs}
								isMulitple={true}
								saveCompsData={saveCompsData}
								versionData={versionData}
								clusterInfo={clusterInfo}
								saveComp={saveComp}
								testConnects={testConnects}
								handleConfirm={handleConfirm}
							/>
						</TabPane>
					);
				})}
			</Tabs>
		</div>
	);
}
