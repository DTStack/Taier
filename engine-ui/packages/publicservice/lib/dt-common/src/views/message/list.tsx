import * as React from 'react';
import { assign } from 'lodash';
import { Tabs, Menu, Table, Checkbox, Button, message } from 'antd';
import { Link } from 'react-router';
import { Utils } from 'dt-utils';

import { getEnableLicenseApp } from '../../funcs';
import utils from '../../utils';
import Api from '../../api';
import MsgStatus from './msgStatus';
import { MsgTypeDesc } from '../../components/display';

const TabPane = Tabs.TabPane;
const MenuItem = Menu.Item;

class MessageList extends React.Component<any, any> {
  state: any = {
    selectedApp: '',
    table: {
      data: [],
    },

    selectedRowKeys: [],
    selectedRows: [],
    selectedAll: false,
  };

  componentDidMount() {
    this.handleStateData();
  }

  handleStateData = () => {
    const { apps, licenseApps = [] } = this.props;
    const initialApp = Utils.getParameterByName('app');
    const defaultApp = licenseApps.find((licapp: any) => licapp.isShow) || [];
    if (apps && apps.length > 0) {
      this.setState(
        {
          selectedApp: initialApp || defaultApp.id,
        },
        this.loadMsg
      );
    }
  };

  componentDidUpdate(prevProps: any, prevState: any) {
    if (
      this.props.licenseApps.length > 0 &&
      prevProps.licenseApps !== this.props.licenseApps
    ) {
      this.handleStateData();
    }
  }

  loadMsg = (params?: any) => {
    const { msgList } = this.props;
    const { selectedApp } = this.state;

    const reqParams = assign(
      {
        currentPage: msgList.currentPage,
        pageSize: 10,
        mode: msgList.msgType,
      },
      params
    );
    if (!selectedApp) return;
    Api.getMessage(selectedApp, reqParams).then((res: any) => {
      if (res.code == 1) {
        this.setState({
          table: res.data,
        });
      }
    });
  };

  getUnreadRows = () => {
    const { selectedRows } = this.state;
    const ids: any = [];

    selectedRows.forEach((item: any) => {
      if (item.readStatus === 0) {
        // 获取未读数据
        ids.push(item.id);
      }
    });

    return ids;
  };

  resetRowKeys = () => {
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
      selectedAll: false,
    });
  };

  markAsRead = () => {
    const { selectedApp } = this.state;

    const unReadRows = this.getUnreadRows();

    if (this.selectedNotNull(unReadRows)) {
      Api.markAsRead(selectedApp, {
        notifyRecordIds: unReadRows,
      }).then((res: any) => {
        if (res.code === 1) {
          this.resetRowKeys();
          this.loadMsg();
        }
      });
    }
  };

  markAsAllRead = () => {
    const { selectedApp } = this.state;

    const unReadRows = this.getUnreadRows();

    Api.markAsAllRead(selectedApp, {
      notifyRecordIds: unReadRows,
    }).then((res: any) => {
      if (res.code === 1) {
        this.resetRowKeys();
        this.loadMsg();
      }
    });
  };

  deleteMsg = () => {
    const ctx = this;
    const { selectedApp, selectedRowKeys } = this.state;
    if (this.selectedNotNull(selectedRowKeys)) {
      Api.deleteMsgs(selectedApp, {
        notifyRecordIds: selectedRowKeys,
      }).then((res: any) => {
        if (res.code === 1) {
          this.props.updateMsg({
            currentPage: 1,
          });
          ctx.setState(
            {
              currentPage: 1,
              selectedAll: false,
              selectedRowKeys: [],
            },
            ctx.loadMsg
          );
        }
      });
    }
  };

  handleTableChange = (pagination: any, filters: any) => {
    this.props.updateMsg({
      currentPage: pagination.current,
    });
    this.setState(
      {
        selectedRowKeys: [],
        selectedAll: false,
      },
      this.loadMsg
    );
  };

  selectedNotNull(selected: any) {
    if (!selected || selected.length <= 0) {
      message.error('请选择要操作的消息！');

      return false;
    }

    return true;
  }

  onPaneChange = (key: any) => {
    this.props.updateMsg({
      currentPage: 1,
      msgType: key,
    });
    this.loadMsg({
      mode: key,
    });
  };

  onAppSelect = ({ key }: any) => {
    this.props.updateMsg({
      currentPage: 1,
    });
    this.setState(
      {
        selectedApp: key,
        selectedRowKeys: [],
      },
      () => {
        this.props.router.replace('/message?app=' + key);
        this.loadMsg();
      }
    );
  };

  onCheckAllChange = (e: any) => {
    const selectedRowKeys = [];
    const selectedRows = [];

    if (e.target.checked) {
      const data = this.state.table.data;

      for (let i = 0; i < data.length; i++) {
        const item = data[i];

        selectedRowKeys.push(item.id);
        selectedRows.push(item);
      }
    }

    this.setState({
      selectedRowKeys,
      selectedRows,
      selectedAll: e.target.checked,
    });
  };

  tableFooter = (currentPageData: any) => {
    const { msgList } = this.props;
    const { selectedAll, table } = this.state;
    const disabled = !table || !table.data || table.data.length === 0;

    return (
      <tr className="ant-table-row  ant-table-row-level-0">
        <td style={{ padding: '0 24px 0px 0px' }}>
          <Checkbox
            checked={selectedAll}
            disabled={disabled}
            onChange={this.onCheckAllChange}></Checkbox>
        </td>
        <td>
          {
            <Button
              size="small"
              type="primary"
              disabled={disabled}
              onClick={this.deleteMsg}>
              删除
            </Button>
          }
          {msgList.msgType !== '3' && (
            <span>
              <Button
                size="small"
                type="primary"
                disabled={disabled}
                onClick={this.markAsRead}>
                标为已读
              </Button>
              <Button
                disabled={disabled}
                size="small"
                type="primary"
                onClick={this.markAsAllRead}>
                全部已读
              </Button>
            </span>
          )}
        </td>
      </tr>
    );
  };

  renderPane = () => {
    const { apps, msgList = {}, licenseApps } = this.props;
    const { table, selectedApp, selectedRowKeys } = this.state;
    const enableApps = getEnableLicenseApp(apps, licenseApps) || [];
    let menuItem = [];
    for (let i = 0; i < enableApps.length; i++) {
      const app = enableApps[i] || [];

      if (
        app.enable &&
        app.id !== 'main' &&
        !app.disableExt &&
        !app.disableMessage
      ) {
        menuItem.push(<MenuItem key={app.id}>{app.name}</MenuItem>);
      }
    }

    const colms = [
      {
        title: '标题与内容',
        dataIndex: 'content',
        key: 'content',
        render(text: any, record: any) {
          return (
            <Link to={`message/detail/${record.id}?app=${selectedApp}`}>
              <MsgStatus value={record.readStatus} /> {text}
            </Link>
          );
        },
      },
      {
        width: 100,
        title: '状态',
        dataIndex: 'readStatus',
        key: 'readStatus',
        render(status: any) {
          let display = '未读';

          if (status === 1) {
            // 已读
            display = '已读';
          }

          return display;
        },
      },
      {
        width: 150,
        title: '发送时间',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        render(text: any) {
          return utils.formatDateTime(text);
        },
      },
      {
        width: 120,
        title: '类型描述',
        dataIndex: 'status',
        key: 'status',
        render(type: any) {
          return MsgTypeDesc(selectedApp, type);
        },
      },
    ];

    const rowSelection = {
      selectedRowKeys,
      onChange: (selectedRowKeys: any, selectedRows: any) => {
        this.setState({
          selectedRowKeys,
          selectedRows,
        });
      },
    };

    const pagination = {
      total: table && table.totalCount,
      defaultPageSize: 10,
      current: msgList.currentPage,
    };

    return (
      <div className="m-panel" style={{ overflowY: 'auto', height: '100%' }}>
        <Menu
          style={{ position: 'relative', zIndex: 10 }}
          selectedKeys={[selectedApp]}
          onSelect={this.onAppSelect}
          className="left">
          {menuItem}
        </Menu>
        <div className="right panel-content">
          <Table
            rowKey="id"
            className="m-table"
            columns={colms}
            dataSource={table.data || []}
            rowSelection={rowSelection}
            onChange={this.handleTableChange}
            pagination={pagination}
            footer={this.tableFooter}
          />
        </div>
      </div>
    );
  };

  render() {
    const { msgList } = this.props;

    const paneContent = this.renderPane();

    return (
      <div
        className="box-1 m-tabs c-messgae__list"
        style={{ height: 'calc(100% - 40px)' }}>
        <Tabs
          animated={false}
          activeKey={msgList.msgType}
          onChange={this.onPaneChange}>
          <TabPane tab="全部消息" key="1">
            {' '}
            {paneContent}{' '}
          </TabPane>
          <TabPane tab="未读消息" key="2">
            {' '}
            {paneContent}{' '}
          </TabPane>
          <TabPane tab="已读消息" key="3">
            {' '}
            {paneContent}{' '}
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

export default MessageList;
