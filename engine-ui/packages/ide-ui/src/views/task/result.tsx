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
import { Pagination, Select } from 'antd';
import { SpreadSheet } from 'dt-react-component';
import molecule from '@dtinsight/molecule/esm';
import './result.scss';

const defaultOutTable = 1;

const Option = Select.Option;

class Result extends React.Component<any, any> {
	state = {
		pagination: {
			current: 1,
			pageSize: 10,
			total: 0,
		},
	};

	componentDidMount() {
		if (this.props.tab?.tableType) {
			this.onPageChange();
		}
	}

	getPageData(data: any) {
		let result: any[] = [];
		if (!data) {
			return result;
		}
		const { pagination } = this.state;
		const { current, pageSize } = pagination;
		const begin = (current - 1) * pageSize;
		const end = begin + pageSize;
		result = data.slice(begin, end);
		return result;
	}

	onPageChange(tableName?: string) {
		const { pagination } = this.state;
		const { tab } = this.props;
		if (tab?.tableType) {
			const newTab = { ...tab, tableName: tableName ?? tab.tableName };
			this.props.getTableData(pagination, newTab, (total: number) => {
				this.setState({ pagination: { ...pagination, total } });
			});
		}
	}

	renderOptions = () => {
		const { tableNameArr } = this.props.tab;
		return tableNameArr.map((name: string) => {
			return (
				<Option key={name} value={name}>
					{name}
				</Option>
			);
		});
	};

	tableNameChange = (tableName: string) => {
		const { tab } = this.props;
		this.props.updateTableData('tableName', tableName, tab.id);
		this.setState(
			(preState: any) => ({
				pagination: { ...preState.pagination, current: 1 },
			}),
			() => this.onPageChange(tableName),
		);
	};

	render() {
		const { pagination } = this.state;
		const { extraView, data, tab } = this.props;
		const showData = data.slice(1, data.length);
		const resultData = tab?.tableType ? showData : this.getPageData(showData);
		const total = !tab?.tableType ? showData.length : pagination.total;
		const pageSizeOptions = !tab?.tableType
			? ['10', '20', '30', '40']
			: ['10', '20', '50', '100'];

		return (
			<molecule.component.Scrollable>
				<div className="c-ide-result">
					{tab?.tableType && (
						<div className="console-select c-ide-result__select">
							<span>
								{tab?.tableType == defaultOutTable ? '数据表：' : '结果表：'}
							</span>
							<Select
								defaultValue={tab.tableName}
								style={{ width: 340 }}
								onChange={this.tableNameChange}
							>
								{this.renderOptions()}
							</Select>
						</div>
					)}
					<div className="c-ide-result__table">
						<SpreadSheet columns={data[0]} data={resultData} />
					</div>
					<div className="c-ide-result__tools">
						{extraView}
						<span className="c-ide-result__mark">
							预览仅显示1000条，点击“下载”获取完整结果
						</span>
						<span className="c-ide-result__tools__pagination">
							<Pagination
								size="small"
								{...pagination}
								total={total}
								showSizeChanger
								pageSizeOptions={pageSizeOptions}
								onChange={(page) => {
									this.setState(
										{
											pagination: {
												...pagination,
												current: page,
											},
										},
										this.onPageChange,
									);
								}}
								onShowSizeChange={(current, size) => {
									this.setState(
										{
											pagination: {
												...pagination,
												pageSize: size,
												current: 1,
											},
										},
										this.onPageChange,
									);
								}}
							/>
						</span>
					</div>
				</div>
			</molecule.component.Scrollable>
		);
	}
}
export default Result;
