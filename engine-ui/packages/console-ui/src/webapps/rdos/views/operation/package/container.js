import React from "react";
import { Card, Tabs } from "antd";
import {hashHistory} from "react-router";

import utils from "utils";
import "../../../styles/pages/package.scss"

import PackageCreate from "./create";
import PackagePublish from "./publish";

const TabPane=Tabs.TabPane;

class PackageContainer extends React.Component {

    state={
        tabKey:1024
    }

    onChange(key){
        const {location} = this.props;
        this.setState({
            tabKey:~~(Math.random()*100000)
        })
        hashHistory.push({pathname:`/package/${key}`,query:location.query})
    }
    render() {
        const {tabKey} = this.state;
        const {params} = this.props;
        const mode=utils.getParameterByName("type")
        const title=`${mode=="realtime"?'实时':'离线'}任务发布`
        return (
            <div
                className="m-tabs box-pd-h"
            >   
                <h1 style={{marginBottom:"20px"}} className="box-title-bolder">{title}</h1>
                <Tabs
                    style={{height:"calc(100% - 44px)"}}
                    className="nav-border"
                    animated={false}
                    onChange={this.onChange.bind(this)}
                    activeKey={params.type}
                    onEdit={this.onEdit}
                    tabBarStyle={{background:"transparent",borderWidth:"0px"}}
                >
                    <TabPane className="m-panel2" tab="创建发布包" key="create">
                        <PackageCreate key={tabKey} mode={mode} />
                    </TabPane>
                    <TabPane className="m-panel2" tab="发布包" key="publish">
                        <PackagePublish key={tabKey} mode={mode} />
                    </TabPane>
                </Tabs>
                {this.props.children}
            </div>
        )
    }
}

export default PackageContainer;