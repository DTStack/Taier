import React from "react";
import { Card, Tabs, Modal, Checkbox, Row, Col } from "antd";

import Api from "../../../../api"
import { publishType } from "../../../../comm/const";

const TabPane = Tabs.TabPane;

class AddLinkModal extends React.Component {

    state = {
        tabKey: '' + publishType.RESOURCE,
        selectList: {
            [publishType.RESOURCE]: [],
            [publishType.FUNCTION]: [],
            [publishType.TABLE]: []
        },
        dataList: {
            [publishType.RESOURCE]: [],
            [publishType.FUNCTION]: [],
            [publishType.TABLE]: []
        },
        checkTask: false
    }
    getLinkItems(id) {
        Api.getTaskLinkItems({
            taskId: id
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            dataList: {
                                [publishType.RESOURCE]: res.data.resources,
                                [publishType.FUNCTION]: res.data.functions,
                                [publishType.TABLE]: res.data.tables
                            }
                        })
                    }
                }
            )
    }
    componentWillReceiveProps(nextProps) {
        const { visible, data, selectedRows } = nextProps;
        const { visible: old_visible } = this.props;
        if (visible && visible != old_visible) {
            this.reset();
            this.getLinkItems(data.id);
            this.initSelect(selectedRows, data);
        }
    }
    onOk() {
        const { selectList, dataList, checkTask } = this.state;
        const { data } = this.props;
        const steps = [publishType.RESOURCE, publishType.FUNCTION, publishType.TABLE];
        setTimeout(
            () => {
                this.props.addNewItem(
                    publishType.TASK,
                    checkTask ? [data] : [],
                    [data]
                )
            }
        )
        for (let step of steps) {
            const stepData = dataList[step];
            const selectData = selectList[step];
            setTimeout(
                () => {
                    this.props.addNewItem(
                        step,
                        stepData.filter(
                            (item) => {
                                return selectData.includes(item.id);
                            }
                        ),
                        stepData
                    )
                }
            )
        }
        this.props.onOk();
    }
    initSelect(rows, data) {
        let tableKeys = [], resourceKeys = [], functionKeys = [];
        for (let i = 0; i < rows.length; i++) {
            const row = rows[i];
            switch (row.itemType) {
                case publishType.RESOURCE: {
                    resourceKeys.push(row.itemId);
                    break;
                }
                case publishType.TABLE: {
                    tableKeys.push(row.itemId);
                    break;
                }
                case publishType.FUNCTION: {
                    functionKeys.push(row.itemId);
                    break;
                }
                case publishType.TASK: {
                    if (data.itemId == row.id) {
                        this.setState({
                            checkTask: true
                        })
                    }
                    break;
                }
            }
        }
        this.setState({
            selectList: {
                [publishType.RESOURCE]: resourceKeys,
                [publishType.FUNCTION]: functionKeys,
                [publishType.TABLE]: tableKeys
            }
        })
    }
    reset() {
        this.setState({
            tabKey: publishType.RESOURCE,
            selectList: {
                [publishType.RESOURCE]: [],
                [publishType.FUNCTION]: [],
                [publishType.TABLE]: []
            },
            dataList: {
                [publishType.RESOURCE]: [],
                [publishType.FUNCTION]: [],
                [publishType.TABLE]: []
            },
            checkTask: false
        })
    }
    onChange(key) {
        this.setState({
            tabKey: key
        })
    }
    renderList(key) {
        const { dataList, selectList } = this.state;
        const { selectedRows } = this.props;

        if (!dataList[key].length) {
            return null;
        }
        const allChecked = selectList[key].length == dataList[key].length;
        const haveChecked = selectList[key].length ? true : false;
        return (
            <Row>
                <Col style={{ marginBottom: "13px" }} span={12}>
                    <Checkbox
                        indeterminate={!allChecked && haveChecked}
                        checked={allChecked}
                        onChange={(e) => {
                            this.setState({
                                selectList: {
                                    ...selectList,
                                    [key]: e.target.checked ? dataList[key].map((item) => item.id) : []
                                }
                            })
                        }}>All</Checkbox>
                </Col>
                {<Checkbox.Group value={selectList[key]} onChange={(checkedValue) => {
                    this.setState({
                        selectList: {
                            ...selectList,
                            [key]: checkedValue
                        }
                    })
                }}>
                    {dataList[key].map(
                        (item) => {
                            let name;
                            switch (key) {
                                case publishType.FUNCTION: {
                                    name = item.name;
                                }
                                case publishType.RESOURCE: {
                                    name = item.resourceName;
                                }
                                case publishType.TABLE: {
                                    name = item.tableName;
                                }
                            }
                            return <Col key={item.id} style={{ marginBottom: "13px" }} span={12}><Checkbox value={item.id}>{name}</Checkbox></Col>
                        }
                    )}
                </Checkbox.Group>}
            </Row>
        )

    }
    render() {
        const { tabKey, dataList, selectList, checkTask } = this.state;
        const { visible, mode, data } = this.props;
        return (
            <Modal
                visible={visible}
                title="添加关联"
                onOk={this.onOk.bind(this)}
                onCancel={this.props.onCancel}
            >
                <p style={{ marginBottom: "25px", color: "#333333" }}>
                    <span>任务：</span>
                    <Checkbox
                        checked={checkTask}
                        onChange={(e) => {
                            this.setState({
                                checkTask: e.target.checked
                            })
                        }} >
                        {data.taskName}
                    </Checkbox>
                    <span style={{ color: "#999999" }}>(2018-01-01 12:12:12)</span>
                </p>

                <div className="m-tabs auto-height">
                    <Tabs
                        className="nav-border no-bottom-border"
                        animated={false}
                        onChange={this.onChange.bind(this)}
                        activeKey={'' + tabKey}
                        tabBarStyle={{ background: "transparent", borderWidth: "0px" }}
                    >
                        <TabPane className="m-panel common-padding" tab="关联资源" key={'' + publishType.RESOURCE}>
                            {this.renderList(publishType.RESOURCE)}
                        </TabPane>
                        <TabPane className="m-panel common-padding" tab="关联函数" key={'' + publishType.FUNCTION}>
                            {this.renderList(publishType.FUNCTION)}
                        </TabPane>
                        {mode == "offline" && <TabPane className="m-panel common-padding" tab="关联表" key={'' + publishType.TABLE}>
                            {this.renderList(publishType.TABLE)}
                        </TabPane>}
                    </Tabs>
                </div>

            </Modal>
        )
    }
}

export default AddLinkModal;