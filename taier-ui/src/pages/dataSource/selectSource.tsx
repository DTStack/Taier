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

import { useCallback, useLayoutEffect, useRef,useState } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Input,Menu, message } from 'antd';
import classNames from 'classnames';
import { debounce } from 'lodash';

import API from '@/api';
import type { IDataSourceType } from './add';
import './selectSource.scss';

const IMG_URL = 'images';
const IMAGE_SIZE = 216;

interface IProps {
    defaultMenu?: string;
    defaultDataSource?: IDataSourceType['dataType'];
    onSelectDataSource?: (dataSource: IDataSourceType, currentMenuId: string) => void;
}

interface IMenuProps {
    classifyId: number;
    classifyCode: string;
    classifyName: string;
    sorted: number;
}

export default function SelectSource({ onSelectDataSource, defaultMenu, defaultDataSource }: IProps) {
    const wrapper = useRef<HTMLDivElement>(null);
    const [searchName, setSearchName] = useState<string | undefined>(undefined);
    const [current, setCurrent] = useState<string[]>(typeof defaultMenu === 'undefined' ? [] : [defaultMenu]);
    const [menuList, setMenuList] = useState<IMenuProps[]>([]);
    const [dataSourceTypeList, setDataSourceList] = useState<IDataSourceType[]>([]);
    const [ratio, setRatio] = useState(1.0);
    const [selectedDataSource, setSelected] = useState(defaultDataSource);

    const getDataSourceList = (dataSourceId: string) => {
        API.queryDsTypeByClassify({
            classifyId: Number(dataSourceId),
            search: searchName || '',
        })
            .then((res) => {
                const { data = [], success } = res;
                if (success) {
                    const nextData = (data as IDataSourceType[]).map((ele) => {
                        return {
                            ...ele,
                            imgUrl: `${IMG_URL}/${ele.imgUrl}`,
                        };
                    });

                    setDataSourceList(nextData);
                }
            })
            .catch(() => {
                message.error('根据分类获取数据源类型失败！');
            });
    };

    const getMenuList = async () => {
        API.queryDsClassifyList({})
            .then((res) => {
                const { data, code } = res;
                if (code === 1) {
                    // set the default menu after getting the menu list
                    const firstMenu = data[0].classifyId;
                    setMenuList(data || []);

                    if (!current.length) {
                        setCurrent([firstMenu.toString()]);
                    }
                    getDataSourceList(current[0] || firstMenu);
                }
            })
            .catch(() => {
                message.error('获取数据源分类类目列表失败！');
            });
    };

    const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
        if (current[0] !== key) {
            setCurrent([key]);
            getDataSourceList(key);
        }
    };

    const handleSearch = () => {
        const defaultMenuId = menuList[0].classifyId.toString();
        if (defaultMenuId !== current[0].toString()) {
            setCurrent([defaultMenuId]);
        }
        getDataSourceList(defaultMenuId);
    };

    // 计算图片缩放倍率
    const computeScaleRatio = useCallback(
        debounce(() => {
            if (wrapper.current) {
                const width = parseFloat(window.getComputedStyle(wrapper.current).width);
                const len = Math.floor(width / (IMAGE_SIZE + 16));
                if (len >= 1) {
                    const nextRatio = (width - 16 * len) / len / IMAGE_SIZE;
                    setRatio(nextRatio);
                } else {
                    setRatio(width / IMAGE_SIZE);
                }
            }
        }, 500),
        []
    );

    // 选择对应类型
    const onSelectType = (item: IDataSourceType) => {
        setSelected(item.dataType);
        onSelectDataSource?.(item, current[0]);
    };

    useLayoutEffect(() => {
        getMenuList();
        computeScaleRatio();
        window.addEventListener('resize', computeScaleRatio);

        return () => {
            window.removeEventListener('resize', computeScaleRatio);
        };
    }, []);

    return (
        <div className="dt-select-source">
            <Input
                style={{ width: 200 }}
                value={searchName}
                placeholder="按数据源名称搜索"
                onChange={(e) => {
                    setSearchName(e.target.value);
                }}
                suffix={<SearchOutlined onClick={() => handleSearch()} style={{ cursor: 'pointer' }} />}
                onPressEnter={() => handleSearch()}
            />
            <div className="show-type">
                <div className="left-menu">
                    <Menu selectedKeys={current} mode="inline" className="dt-datasource-menu" onClick={handleMenuClick}>
                        {menuList.map((item) => {
                            return (
                                <Menu.Item key={item.classifyId.toString()}>
                                    <span>{item.classifyName}</span>
                                </Menu.Item>
                            );
                        })}
                    </Menu>
                </div>
                <div className="right-menu">
                    <div className="right-menu-main">
                        <div className="right-menu-main-content" ref={wrapper}>
                            {dataSourceTypeList.map((item) => {
                                const col = (
                                    <div
                                        className="right-menu-item"
                                        key={item.dataType}
                                        style={{
                                            width: 'auto',
                                            marginBottom: 20,
                                        }}
                                        onClick={() => onSelectType(item)}
                                    >
                                        <img
                                            src={item.imgUrl}
                                            alt="图片显示失败"
                                            className={classNames(selectedDataSource === item.dataType && 'selected')}
                                            style={{
                                                width: IMAGE_SIZE * ratio,
                                                height: 108 * ratio,
                                            }}
                                        />
                                        <p
                                            style={{
                                                textAlign: 'center',
                                                marginTop: 12,
                                            }}
                                        >
                                            {item.dataType}
                                        </p>
                                    </div>
                                );
                                return col;
                            })}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
