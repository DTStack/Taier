import React from 'react';
import { Content, Header } from 'molecule/esm/workbench/sidebar';
import FolderTree from 'molecule/esm/workbench/sidebar/explore/folderTree';
import './index.scss';
import { ActionBar } from 'molecule/esm/components';

export default ({ treeData, panel, headerToolBar }) => {
    return (
        <div className="resourceManager-container">
            <Header
                title={'资源管理'}
                toolbar={<ActionBar data={headerToolBar} />}
            />
            <Content>
                <div tabIndex={0} className="resourceManager-content">
                    <FolderTree
                        panel={panel}
                        onUpdateFileName={() => {}}
                        onSelectFile={() => {}}
                        onDropTree={() => {}}
                        onClickContextMenu={() => {}}
                        onRightClick={() => {}}
                        createTreeNode={() => {}}
                        folderTree={treeData}
                    />
                </div>
            </Content>
        </div>
    );
};
