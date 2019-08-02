import * as React from 'react';
import { Tabs } from 'antd';

import PaneOne from './paneOne';
import PaneTwo from './paneTwo';
import PaneThree from './paneThree';
import PaneFour from './paneFour';
import PaneFive from './paneFive';
import PaneSix from './paneSix';
import PaneSeven from './paneSeven';

const TabPane = Tabs.TabPane;

export default class DMConfigureCenter extends React.Component<any, any> {
    render () {
        return (
            <div className="box-1 m-tabs">
                <Tabs animated={false} style={{ height: 'auto', minHeight: 'calc(100% - 40px)' }}>
                    <TabPane tab="模型层级" key="1">
                        <PaneOne />
                    </TabPane>
                    <TabPane tab="主题域" key="2">
                        <PaneTwo />
                    </TabPane>
                    <TabPane tab="刷新频率定义" key="3">
                        <PaneThree />
                    </TabPane>
                    <TabPane tab="增量定义" key="4">
                        <PaneFour />
                    </TabPane>
                    <TabPane tab="表名生成规则" key="5">
                        <PaneFive />
                    </TabPane>
                    <TabPane tab="原子指标定义" key="6">
                        <PaneSix />
                    </TabPane>
                    <TabPane tab="衍生指标定义" key="7">
                        <PaneSeven />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
