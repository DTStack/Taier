import * as React from 'react';
import { Dropdown, Input, Button, Menu, Icon, Table } from 'antd';
import './style.scss';

const Search = Input.Search;
interface IProps {}

interface IState {
    data:Array<any>;
}

export default class LabelManage extends React.PureComponent<IProps, IState> {
    state: IState = {
         data: [{
            key: 1,
            name: 'John Brown sr.',
            age: 60,
            address: 'New York No. 1 Lake Park',
            children: [{
              key: 11,
              name: 'John Brown',
              age: 42,
              address: 'New York No. 2 Lake Park',
            }, {
              key: 12,
              name: 'John Brown jr.',
              age: 30,
              address: 'New York No. 3 Lake Park',
              children: [{
                key: 121,
                name: 'Jimmy Brown',
                age: 16,
                address: 'New York No. 3 Lake Park',
              }],
            }, {
              key: 13,
              name: 'Jim Green sr.',
              age: 72,
              address: 'London No. 1 Lake Park',
              children: [{
                key: 131,
                name: 'Jim Green',
                age: 42,
                address: 'London No. 2 Lake Park',
                children: [{
                  key: 1311,
                  name: 'Jim Green jr.',
                  age: 25,
                  address: 'London No. 3 Lake Park',
                }, {
                  key: 1312,
                  name: 'Jimmy Green sr.',
                  age: 18,
                  address: 'London No. 4 Lake Park',
                }],
              }],
            }],
          }, {
            key: 2,
            name: 'Joe Black',
            age: 32,
            address: 'Sidney No. 1 Lake Park',
          }]
        
    }
    render () {
        const menu = (
            <Menu>
                <Menu.Item key="0">
                    <a href="http://www.alipay.com/">1st menu item</a>
                </Menu.Item>
                <Menu.Item key="1">
                    <a href="http://www.taobao.com/">2nd menu item</a>
                </Menu.Item>
                <Menu.Divider />
                <Menu.Item key="3">3rd menu item</Menu.Item>
            </Menu>
        );
        const columns = [{
            title: '一级',
            dataIndex: 'name',
            key: 'name', 
          },
          {
            title: '创建者',
            dataIndex: 'age',
            key: 'age',
            width:200
          }, {
            title: '操作',
            dataIndex: 'address',
            key: 'address',
            width:220,
            render:() => {
                return <div>
                    <a>新建子目录</a>
                    <span className="ant-divider" />
                    <a>重命名</a>
                    <span className="ant-divider" />
                    <a>移动</a>
                    <span className="ant-divider" />
                    <a>删除</a>
                </div>
            }
          }];
          
        return (
            <div className="labelManage">
                <div className="title_wrap">
                    <div className="left_wp">
                        <span>实体-</span>
                        <Dropdown overlay={menu}>
                            <a className="ant-dropdown-link" href="javasript:viod();">
                              用户信息<Icon type="down" />
                            </a>
                        </Dropdown>
                    </div>
                    <div className="right_wp">
                        <Search
                            placeholder="搜索目录名称"
                            className="search"
                            onSearch={value => console.log(value)}
                        />
                        <Button type="primary">新建目录</Button>
                    </div>
                </div>
                <div className="draggable-wrap-table">
                   <Table columns={columns} dataSource={data} />
                </div>
            </div>
        );
    }
}
