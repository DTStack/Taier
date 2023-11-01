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
import { ExclamationCircleOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import { getEventPosition } from '@dtinsight/molecule/esm/common/dom';
import { ActionBar, Menu, useContextViewEle } from '@dtinsight/molecule/esm/components';
import { connect } from '@dtinsight/molecule/esm/react';
import { Content, Header } from '@dtinsight/molecule/esm/workbench/sidebar';
import { Empty, message, Modal } from 'antd';
import classNames from 'classnames';

import API from '@/api';
import { DetailInfoModal } from '@/components/detailInfo';
import { DataSourceLinkFailed, DataSourceLinkSuccess } from '@/components/icon';
import { ID_COLLECTIONS } from '@/constant';
import type { IDataSourceProps } from '@/interface';
import { dataSourceService } from '@/services';
import type { IDataSourceState } from '@/services/dataSourceService';
import Add from './add';
import Search from './search';
import './index.scss';

const { confirm } = Modal;

interface IOther {
    search: string;
    dataTypeList: string[];
}

const DataSourceView = ({ dataSource }: IDataSourceState) => {
    const [other, setOther] = useState<IOther>({
        search: '',
        dataTypeList: [],
    });

    const [visible, setVisible] = useState<boolean>(false);
    const [detailView, setView] = useState<IDataSourceProps | undefined>(undefined);

    const contextView = useContextViewEle();

    // 搜索事件
    const handleSearch = (value: Record<string, any>) => {
        const data = { ...other, ...value };
        setOther(data);
    };

    const handleOpenDetail = (record: IDataSourceProps) => {
        setVisible(true);
        setView(record);
    };

    // 删除
    const toDelete = async (record: IDataSourceProps) => {
        const { success, message: msg } = await API.dataSourceDelete({
            dataInfoId: record.dataInfoId,
        });

        if (success) {
            message.success('删除成功');
            // 更新表格
            dataSourceService.reloadDataSource();
        } else {
            message.error(`${msg}`);
        }
    };

    const handleMenuClick = (menu: { id: string; name: string }, record: IDataSourceProps) => {
        contextView?.hide();
        switch (menu.id) {
            case 'edit':
                if (molecule.editor.isOpened(ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX)) {
                    message.warning('请先保存或关闭编辑数据源');
                    const groupId = molecule.editor.getGroupIdByTab(ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX)!;
                    molecule.editor.setActive(groupId, ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX);
                } else {
                    molecule.editor.open({
                        id: ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
                        name: '编辑数据源',
                        icon: 'edit',
                        renderPane: (
                            <Add
                                key={ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX}
                                record={record}
                                onSubmit={() => dataSourceService.reloadDataSource()}
                            />
                        ),
                        breadcrumb: [
                            {
                                id: 'root',
                                name: '数据源中心',
                            },
                            {
                                id: ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
                                name: '编辑数据源',
                            },
                        ],
                    });
                }
                break;
            case 'delete':
                confirm({
                    title: '是否删除此条记录？',
                    icon: <ExclamationCircleOutlined />,
                    okText: '删除',
                    okType: 'danger',
                    cancelText: '取消',
                    onOk() {
                        toDelete(record);
                    },
                    onCancel() {},
                });
                break;
            default:
                break;
        }
    };

    const handleContextmenu = (e: React.MouseEvent<HTMLLIElement, MouseEvent>, record: IDataSourceProps) => {
        e.preventDefault();
        e.currentTarget.focus();
        contextView?.show(getEventPosition(e), () => (
            <Menu
                role="menu"
                onClick={(_: any, item: any) => handleMenuClick(item, record)}
                data={[
                    {
                        id: 'edit',
                        name: '编辑',
                    },
                    {
                        id: 'delete',
                        name: '删除',
                    },
                ]}
            />
        ));
    };

    const handleHeaderBarClick = () => {
        if (molecule.editor.isOpened(ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX)) {
            message.warning('请先保存或关闭新增数据源');
            const groupId = molecule.editor.getGroupIdByTab(ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX)!;
            molecule.editor.setActive(groupId, ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX);
        } else {
            molecule.editor.open({
                id: ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
                name: '新增数据源',
                icon: 'server-process',
                renderPane: (
                    <Add
                        key={ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX}
                        onSubmit={() => dataSourceService.reloadDataSource()}
                    />
                ),
                breadcrumb: [
                    {
                        id: 'root',
                        name: '数据源中心',
                    },
                    {
                        id: ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
                        name: '新增数据源',
                    },
                ],
            });
        }
    };

    const renderFilterDataSource = (item: IDataSourceProps) => {
        if (other.search) {
            return item.dataName.includes(other.search);
        }

        if (other.dataTypeList?.length) {
            return other.dataTypeList.includes(item.dataType);
        }

        return true;
    };

    const filterDataSource = useMemo(() => dataSource.filter(renderFilterDataSource), [dataSource, other]);

    return (
        <div className="datasource-container">
            <Header
                title="数据源中心"
                toolbar={
                    <ActionBar
                        data={[
                            {
                                id: 'add',
                                title: '新增数据源',
                                icon: 'server-process',
                                contextMenu: [],
                                onClick: handleHeaderBarClick,
                            },
                        ]}
                    />
                }
            />
            <Content>
                <Search onSearch={handleSearch} />
                {filterDataSource.length ? (
                    <div tabIndex={0} className="datasource-content">
                        <ul className="datasource-list">
                            {filterDataSource.map((item) => (
                                <li
                                    key={item.dataInfoId}
                                    tabIndex={-1}
                                    className="datasource-record"
                                    onClick={() => handleOpenDetail(item)}
                                    onContextMenu={(e) => handleContextmenu(e, item)}
                                >
                                    {item.status === 0 ? (
                                        <DataSourceLinkFailed style={{ color: '#ed5b56', fontSize: 0 }} />
                                    ) : (
                                        <DataSourceLinkSuccess style={{ color: '#72c140', fontSize: 0 }} />
                                    )}
                                    <div className="datasource-title">
                                        <span className="title" title={item.dataName}>
                                            {item.dataName}({item.dataType}
                                            {item.dataVersion || ''})
                                        </span>
                                        <span className={classNames('desc')}>{item.dataDesc || '--'}</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                ) : (
                    <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                )}
                <DetailInfoModal
                    type="dataSource"
                    title="数据源详情"
                    visible={visible}
                    loading={false}
                    onCancel={() => setVisible(false)}
                    data={detailView}
                />
            </Content>
        </div>
    );
};

export default connect(dataSourceService, DataSourceView);
