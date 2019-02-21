import React from 'react'
import { Tabs } from 'antd'

const TabPane = Tabs.TabPane

export default function AppTabs (props) {
    const { apps, content, onPaneChange, activeKey } = props
    const enableApps = apps.filter(app => app.enable && app.id !== 'main')

    const tabPanes = enableApps.length > 0 && enableApps.map(app => {
        const isShow = !app.disableExt && !app.disableSetting;

        return isShow && (<TabPane tab={app.name} key={app.id} data={app}>
            {content}
        </TabPane>)
    });

    return (
        <div className="m-tabs">
            {
                enableApps.length < 2 ? content
                    : <Tabs
                        animated={false}
                        activeKey={activeKey}
                        onChange={onPaneChange}
                    >
                        {tabPanes}
                    </Tabs>
            }
        </div>
    )
}
