import React from "react";
import { Card, Modal, Spin } from "antd";

import CodeEditor from "widgets/code-editor";
class ApiSqlEditor extends React.Component {
    state = {
        sql: "",
        loading: false
    }
    showModal() {
        const centerStyle = { textAlign: "center" };
        const rightStyle = { float: "right", width: "200px", color: "#333" }
        const clearStyle = { overflow: "hidden" }
        Modal.info({
            title: 'SQL编写提示',
            width: "500px",
            content: (
                <div>
                    <p>SQL示例语句:</p>
                    <p>SELECT</p>
                    <p style={clearStyle}>sales time，<span style={rightStyle}>SELECT查询的字段即为API返回参数。</span></p>
                    <p style={clearStyle}>addr as address, <span style={rightStyle}>如果定义了字段别名，则返回参数名称为字段别名。</span></p>
                    <p style={clearStyle}>sum(value) as total_amount <span style={rightStyle}> 支持SQL中sum、count等函数。</span></p>
                    <br />
                    <p>FROM</p>
                    <p>table_user</p>
                    <br />
                    <p>WHERE</p>
                    <p style={clearStyle}>user_id = ${'{'}uid}；<span style={rightStyle}>WHERE条件中的参数为API请求参数，参数格式必须为${'{'}参数名}。</span></p>
                    <br />
                    <p>注意：</p>
                    <p>1.只支持输入一条完整的SQL语句；</p>
                    <p>2.不支持子语句查询；</p>
                    <p>3.支持同一数据源下的两张表关联查询；</p>
                    <p>4.SQL编写完成后，可对参数信息进行设置，填写参数说明、打开分页查询按钮等，参数设置完毕后可进入测试环节。</p>
                </div>
            ),
            onOk() { },
        });
    }
    renderTitle() {
        const { loading } = this.props;
        return (
            <span>API配置SQL语句
                <a style={{ marginLeft: "20px", fontSize: "12px" }} onClick={this.props.sqlFormat}>格式化语句</a>
                <a style={{ marginLeft: "8px", fontSize: "12px" }} onClick={this.showModal}>SQL编写提示</a>
                <a style={{ float: "right", marginLeft: "8px", fontSize: "12px" }} onClick={() => {
                    if (loading) {
                        return;
                    }
                    this.props.sqlModeShowChange()
                }}>编辑参数</a>
                <span style={{ float: "right", fontSize: "12px", color: "#888" }}>{loading && <Spin size="small" style={{ marginRight: "8px" }} />}代码</span>
            </span>
        )
    }
    render() {
        return (
            <div>
                <Card
                    noHovering
                    title={this.renderTitle()}
                >
                    <CodeEditor
                        style={{ height: "600px", marginTop: "1px" }}
                        onChange={this.props.sqlOnChange}
                        value={this.props.editor.sql}
                        cursor={this.props.editor.cursor}
                        sync={this.props.editor.sync}
                    />
                </Card>
            </div>
        )
    }
}

export default ApiSqlEditor;