import React from "react";
import { Card, Tabs, Modal, Checkbox, Row, Col } from "antd";



const TabPane = Tabs.TabPane;

class AddLinkModal extends React.Component {

    state = {
        tabKey: "resource",
        selectList: {
            resource: [1],
            func: [3, 4],
            table: [3]
        },
        dataList: {
            resource: [1, 2, 3],
            func: [3, 4, 5],
            table: [2, 4, 5]
        }
    }
    componentWillReceiveProps(nextProps) {
        const { visible } = nextProps;
        const { visible: old_visible } = this.props;
        if (visible && visible != old_visible) {
            this.reset();
        }
    }
    reset(){
        this.setState({
            tabKey: "resource",
            selectList: {
                resource: [],
                func: [],
                table: []
            },
            dataList: {
                resource: [],
                func: [],
                table: []
            }
        })
    }
    onChange(key) {
        this.setState({
            tabKey: key
        })
    }
    renderList(key) {
        const { dataList, selectList } = this.state;
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
                                    [key]: e.target.checked ? dataList[key] : []
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
                            return <Col key={item} style={{ marginBottom: "13px" }} span={12}><Checkbox checked value={item}>{item}</Checkbox></Col>
                        }
                    )}
                </Checkbox.Group>}
            </Row>
        )

    }
    render() {
        const { tabKey, dataList, selectList } = this.state;
        const { visible } = this.props;
        return (
            <Modal
                visible={visible}
                title="添加关联"
                onOk={this.props.onOk}
                onCancel={this.props.onCancel}
            >
                <p style={{ marginBottom: "25px", color: "#333333" }}>
                    <span>任务：</span>
                    <Checkbox >taskName</Checkbox>
                    <span style={{ color: "#999999" }}>(2018-01-01 12:12:12)</span>
                </p>

                <div className="m-tabs auto-height">
                    <Tabs
                        className="nav-border no-bottom-border"
                        animated={false}
                        onChange={this.onChange.bind(this)}
                        activeKey={tabKey}
                        tabBarStyle={{ background: "transparent", borderWidth: "0px" }}
                    >
                        <TabPane className="m-panel common-padding" tab="关联资源" key="resource">
                            {this.renderList("resource")}
                        </TabPane>
                        <TabPane className="m-panel common-padding" tab="关联函数" key="function">
                            {this.renderList("func")}
                        </TabPane>
                        <TabPane className="m-panel common-padding" tab="关联表" key="table">
                            {this.renderList("table")}
                        </TabPane>
                    </Tabs>
                </div>

            </Modal>
        )
    }
}

export default AddLinkModal;