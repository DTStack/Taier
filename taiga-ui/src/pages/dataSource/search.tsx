import { useEffect, useState } from 'react';
import { Form, Checkbox, Select, Tooltip, message, Input } from 'antd';
import { API } from '@/api/dataSource';
import { SearchOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import './search.scss';

interface IProps {
	onSearch: (value: IFormFieldProps) => void;
}

const { Option } = Select;

interface ITypeProps {
	dataType: string;
}

interface IFormFieldProps {
	search: string;
	dataTypeList: string[];
	isMeta: number;
}

const ALL = '全部';

export default function Search({ onSearch }: IProps) {
	const [form] = Form.useForm<IFormFieldProps>();
	const [typeList, setTypeList] = useState<ITypeProps[]>([]);

	const handleSearch = (params: Partial<IFormFieldProps> = {}) => {
		const { search = '', dataTypeList, isMeta } = form.getFieldsValue();
		onSearch({
			search: search.trim(),
			dataTypeList: dataTypeList.includes(ALL) ? [] : dataTypeList,
			isMeta,
			...params,
		});
	};

	const getTypeList = async () => {
		const { data, success } = await API.typeList({});

		if (success) {
			if (Array.isArray(data)) {
				data.unshift({
					dataType: ALL,
				});
			}
			setTypeList(data || []);
		} else {
			message.error('获取类型下拉框内容失败！');
		}
	};

	const handleSelectType = (value: string[]) => {
		if (value.includes(ALL)) {
			form.setFieldsValue({ dataTypeList: [ALL] });
			handleSearch({ dataTypeList: [] });
		} else {
			handleSearch({ dataTypeList: value });
		}
	};

	useEffect(() => {
		getTypeList();
	}, []);

	return (
		<div className="top-search">
			<Form<IFormFieldProps>
				form={form}
				wrapperCol={{ span: 24 }}
				autoComplete='off'
				initialValues={{
					dataTypeList: [ALL],
				}}
			>
				<Form.Item name="search">
					<Input
						placeholder="数据源名称/描述"
						onPressEnter={() => handleSearch()}
						suffix={
							<SearchOutlined
								onClick={() => handleSearch()}
								style={{ cursor: 'pointer' }}
							/>
						}
					/>
				</Form.Item>
				<Form.Item
					noStyle
					shouldUpdate={(prev, next) => prev.dataTypeList !== next.dataTypeList}
				>
					{({ getFieldValue }) => (
						<Form.Item name="dataTypeList">
							<Select<string[]>
								mode="multiple"
								placeholder="请选择类型"
								allowClear
								showSearch
								maxTagCount={1}
								showArrow={true}
								optionFilterProp="children"
								onChange={handleSelectType}
							>
								{typeList.map((item) => {
									return (
										<Option
											disabled={
												getFieldValue('dataTypeList').includes(ALL) &&
												item.dataType !== ALL
											}
											value={item.dataType}
											key={item.dataType}
										>
											{item.dataType}
										</Option>
									);
								})}
							</Select>
						</Form.Item>
					)}
				</Form.Item>
				<Form.Item>
					<Form.Item name="isMeta" valuePropName="checked" noStyle initialValue={0}>
						<Checkbox
							onChange={(e) => handleSearch({ isMeta: Number(e.target.checked) })}
						>
							显示默认数据库
						</Checkbox>
					</Form.Item>
					<Tooltip title="各模块在创建项目时的默认数据源">
						<QuestionCircleOutlined color="#999" />
					</Tooltip>
				</Form.Item>
			</Form>
		</div>
	);
}
