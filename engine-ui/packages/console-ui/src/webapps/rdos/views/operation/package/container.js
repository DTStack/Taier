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

    }
    onChange(key){
        const {location} = this.props;

        hashHistory.push({pathname:`/package/${key}`,query:location.query})
    }
    render() {
        const {params} = this.props;
        const mode=utils.getParameterByName("type")
        const title=`${mode=="realtime"?'实时':'离线'}任务发布`
        return (
            <div
                className="m-tabs box-pd-h"
            >
                <Tabs
                    className="nav-border"
                    animated={false}
                    onChange={this.onChange.bind(this)}
                    activeKey={params.type}
                    onEdit={this.onEdit}
                    tabBarStyle={{background:"transparent",borderWidth:"0px"}}
                >
                    <TabPane className="m-panel2" tab="创建发布包" key="create">
                        <PackageCreate mode={mode} />
                    </TabPane>
                    <TabPane className="m-panel2" tab="发布包" key="publish">
                        <PackagePublish mode={mode} />
                    </TabPane>
                </Tabs>
                {this.props.children}
            </div>
        )
    }
}

export default PackageContainer;