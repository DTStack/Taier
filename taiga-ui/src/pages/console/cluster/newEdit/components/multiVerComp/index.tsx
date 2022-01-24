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

import { useMemo, useState } from 'react';
import type { FormInstance } from 'antd';
import { Tabs, Menu, Dropdown, Button, Radio, Row, Col } from 'antd';
import { DownOutlined, CaretRightOutlined } from '@ant-design/icons';
import TestRestIcon from '@/components/testResultIcon';
import {
	VERSION_TYPE,
	COMP_ACTION,
	COMPONENT_CONFIG_NAME,
	FLINK_DEPLOY_TYPE,
	FLINK_DEPLOY_NAME,
} from '../../const';
import { isFLink } from '../../help';
import FileConfig from '../../fileConfig';
import FormConfig from '../../formConfig';
import ToolBar from '../toolbar';
import './index.scss';

const { TabPane } = Tabs;
const MenuItem = Menu.Item;

/** getComponentVersion 接口获取 versionData 的数组类型 */
interface VersionInfo {
	deployTypes: number[];
	key: string;
	value: string;
}

interface IProps {
	comp: any;
	form: FormInstance;
	view: boolean;
	saveCompsData: any[];
	versionData: Record<string, VersionInfo[]>;
	clusterInfo: any;
	testStatus: any;
	saveComp: (params: any, type?: string) => void;
	getLoadTemplate: (key?: string, params?: any) => void;
	handleConfirm: (action: string, comps: any | any[], mulitple?: boolean) => void;
	testConnects: Function;
}

const className = 'c-multiVersionComp';

export default function MultiVersionComp({
	comp,
	form,
	view,
	saveCompsData,
	versionData,
	clusterInfo,
	testStatus,
	saveComp,
	getLoadTemplate,
	handleConfirm,
	testConnects,
}: IProps) {
	const [deployType, setDeployType] = useState(
		comp?.multiVersion[0]?.deployType ?? FLINK_DEPLOY_TYPE.YARN,
	);

	const handleMenuClick = (e: any) => {
		const typeCode = comp?.componentTypeCode ?? '';

		saveComp(
			{
				componentTypeCode: typeCode,
				hadoopVersion: e.key,
				deployType,
				isDefault: false,
			},
			COMP_ACTION.ADD,
		);
		getLoadTemplate(typeCode, { compVersion: e.key, deployType });
	};

	const getMeunItem = (displayVersion: VersionInfo[]) => {
		const typeCode = comp?.componentTypeCode ?? '';

		return (
			<Menu onClick={handleMenuClick}>
				{displayVersion?.map(({ value }) => {
					const disabled = comp?.multiVersion?.findIndex(
						(vcomp: any) => vcomp.hadoopVersion === value,
					);
					return (
						<MenuItem disabled={disabled > -1} key={value}>
							{isFLink(typeCode)
								? FLINK_DEPLOY_NAME[deployType]
								: (COMPONENT_CONFIG_NAME as any)[typeCode]}{' '}
							{getCompVersion(value)}
						</MenuItem>
					);
				})}
			</Menu>
		);
	};

	const addMultiVersionComp = (value: string) => {
		const typeCode = comp?.componentTypeCode ?? '';

		saveComp(
			{
				deployType,
				componentTypeCode: typeCode,
				hadoopVersion: value,
				isDefault: true,
			},
			COMP_ACTION.ADD,
		);
		getLoadTemplate(typeCode, { compVersion: value, deployType });
	};

	const getCompVersion = (value: string) => {
		const keep2Decimal = ['110', '112'];
		return (Number(value) / 100).toFixed(keep2Decimal.includes(value) ? 2 : 1);
	};

	const getComponentName = (typeCode: number, type: number = FLINK_DEPLOY_TYPE.YARN) => {
		if (isFLink(typeCode)) return FLINK_DEPLOY_NAME[type];
		return (COMPONENT_CONFIG_NAME as any)[typeCode];
	};

	const getDefaultVerionCompStatus = (component: any) => {
		/** 当flink组件只有一个组件版本时勾选为默认版本 */
		const typeCode = component?.componentTypeCode ?? '';
		if (isFLink(typeCode) && component.multiVersion.length === 1) return true;
		return false;
	};

	const typeCode = comp?.componentTypeCode ?? '';
	const isDefault = useMemo(() => getDefaultVerionCompStatus(comp), [comp]);

	// flink 由后端 deployTypes 字段控制展示版本
	const displayVersion = useMemo(() => {
		let tempVersion = versionData[(VERSION_TYPE as any)[typeCode]] || [];
		if (isFLink(typeCode)) {
			tempVersion = tempVersion.filter((item) => item?.deployTypes?.includes(deployType));
		}
		return tempVersion;
	}, [typeCode]);

	if (!comp?.multiVersion[0]?.hadoopVersion) {
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
									<Radio value={FLINK_DEPLOY_TYPE.STANDALONE}>
										{FLINK_DEPLOY_NAME[FLINK_DEPLOY_TYPE.STANDALONE]}
									</Radio>
								</Radio.Group>
							</Col>
						</Row>
					)}
					<Row className={`${className}__intail__row`}>
						<Col span={10}>选择版本：</Col>
						<Col style={{ display: 'flex' }}>
							{displayVersion?.map(({ key, value }) => {
								return (
									<div
										key={key}
										className={`${className}__intail__container__desc`}
										onClick={() => addMultiVersionComp(value)}
									>
										<span className="comp-name">
											<img
												src={`public/img/${
													(VERSION_TYPE as any)[typeCode]
												}.png`}
											/>
											<span>
												{!isFLink(typeCode) &&
													`${(COMPONENT_CONFIG_NAME as any)[typeCode]} `}
												{getCompVersion(value)}
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
						form={form}
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
				className={`${className}__tabs`}
				tabBarExtraContent={
					<Dropdown
						disabled={view}
						overlay={() => getMeunItem(displayVersion)}
						placement="bottomCenter"
					>
						<Button type="primary" size="small" style={{ marginRight: 20 }}>
							添加版本
							<DownOutlined />
						</Button>
					</Dropdown>
				}
			>
				{comp?.multiVersion.map((vcomp: any) => {
					const { deployType: type, componentTypeCode, hadoopVersion } = vcomp;
					return (
						<TabPane
							tab={
								<span>
									{getComponentName(componentTypeCode, type)}
									{getCompVersion(hadoopVersion)}
									<TestRestIcon
										testStatus={testStatus.find(
											(status: any) =>
												status?.componentVersion === hadoopVersion,
										)}
									/>
								</span>
							}
							key={String(hadoopVersion)}
						>
							<>
								<FileConfig
									comp={vcomp}
									form={form}
									view={view}
									isDefault={isDefault}
									saveCompsData={saveCompsData}
									versionData={versionData}
									clusterInfo={clusterInfo}
									saveComp={saveComp}
								/>
								<FormConfig comp={vcomp} view={view} form={form} />
								{!view && (
									<ToolBar
										mulitple={true}
										comp={vcomp}
										clusterInfo={clusterInfo}
										form={form}
										saveComp={saveComp}
										testConnects={testConnects}
										handleConfirm={handleConfirm}
									/>
								)}
							</>
						</TabPane>
					);
				})}
			</Tabs>
		</div>
	);
}
