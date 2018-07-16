import React from "react";
import { Card, Table, Modal, Select } from "antd";
import {cloneDeep} from "lodash";


const Option = Select.Option;

class ChangeResourceModal extends React.Component {
    state = {
        loading:false,
        userList: [{ name: "z", id: 1 }, { name: "x", id: 2 }],
        selectUserMap: {},
        selectUser: "",//select输入value
    }
    componentDidMount() {

    }
    componentWillReceiveProps(nextProps){
        const {resource:nextResource,visible:nextVisible} = nextProps;
        const {resource,visible} = this.props;
        if(visible!=nextVisible&&nextVisible){
            this.setState({
                userList:[],
                selectUserMap:{},
                selectUser:"",
                loading:false
            })
        }
    }
    changeUserValue(value) {
        this.setState({
            selectUser: value
        })
    }
    selectUser(value, option) {
        const { selectUserMap } = this.state;
        this.setState({
            selectUser: "",
            selectUserMap: {
                ...selectUserMap,
                [value]:{
                    name:option.props.children
                }
            }
        })
    }
    removeUser(id){
        let { selectUserMap } = this.state; 
        selectUserMap=cloneDeep(selectUserMap);
        delete selectUserMap[id];
        this.setState({
            selectUserMap:selectUserMap
        })
    }
    getUserOptions() {
        const { userList, selectUserMap } = this.state;
        const result = [];
        for (let i = 0; i < userList.length; i++) {
            const user = userList[i];
            if (!selectUserMap[user.id]) {
                result.push(<Option value={user.id}>{user.name}</Option>)
            }
        }
        return result;
    }
    initColumns(){
        return [
            {
                title:"租户名称",
                dataIndex:"name",
                width:"150px"
            },
            {
                title:"操作",
                dataIndex:"deal",
                render:(text,record)=>{
                    return (<a onClick={this.removeUser.bind(this,record.id)}>删除</a>)
                }
            }
        ]
    }
    getTableDataSource(){
        const {selectUserMap} = this.state;
        const keyAndValue=Object.entries(selectUserMap);
        return keyAndValue.map((item)=>{
            return {
                id:item[0],
                name:item[1].name
            }
        })

    }
    changeResource(){
        this.setState({
            loading:true
        })
        const {selectUserMap} = this.state;
        const {resource} = this.props;
        
    }
    render() {
        const { selectUser,loading } = this.state;
        const { visible,resource } = this.props;
        const columns= this.initColumns();
        const {queueName} = resource;
      
        return (
            <div className="contentBox">
                <Modal
                    title="修改"
                    visible={visible}
                    onCancel={this.props.onCancel}
                    onOk={this.changeResource.bind(this)}
                    confirmLoading={loading}
                >
                    <div className="line-formItem">资源队列：{queueName}</div>
                    <div className="line-formItem">绑定租户：
                        <Select
                            mode="combobox"
                            style={{ width: "150px" }}
                            placeholder="请选择租户"
                            onSelect={this.selectUser.bind(this)}
                            onSearch={this.changeUserValue.bind(this)}
                            value={selectUser}
                        >
                            {this.getUserOptions()}
                        </Select>
                    </div>
                    <Table
                    className="m-table"
                    style={{margin:"0 auto",marginTop:"20px",width:"300px"}}
                    columns={columns}
                    pagination={false}
                    dataSource={this.getTableDataSource()}
                    scroll={{y:300}}
                    />
                </Modal>
            </div>
        )
    }
}

export default ChangeResourceModal;