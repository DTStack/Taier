import React from 'react';
import { Content, Header } from 'molecule/esm/workbench/sidebar';
import { FolderTree } from 'molecule/esm/workbench/sidebar/explore/index';
import { ActionBar, IMenuItemProps } from 'molecule/esm/components';
import molecule from 'molecule/esm';
import './index.scss';
import { COMMON_CONTEXT_MENU } from 'molecule/esm/model';
import { connect } from 'molecule/esm/react';
import resourceManagerTree from '../../services/resourceManagerService';

const FolderTreeView = connect(resourceManagerTree, FolderTree);

interface IResourceProps {
    panel: any;
    headerToolBar: any[];

    onUpload?: () => void;
    onReplace?: () => void;
    onCreateFolder?: () => void;
}

export default ({
    panel,
    headerToolBar,
    onUpload,
    onCreateFolder,
    onReplace,
}: IResourceProps) => {
    const handleHeaderContextClick = (
        e: React.MouseEvent<Element, MouseEvent>,
        item?: IMenuItemProps
    ) => {
        e.preventDefault();
        switch (item?.id) {
            case 'upload':
                onUpload?.();
                break;
            case 'replace':
                onReplace?.();
                break;
            case 'create-folder':
                onCreateFolder?.();
                break;
            default:
                break;
        }
    };

    const handleRightClick = () => {
        const menus: IMenuItemProps[] = COMMON_CONTEXT_MENU.concat();
        return menus.concat(molecule.folderTree.getFolderContextMenu());
    };

    return (
        <div className="resourceManager-container">
            <Header
                title={'资源管理'}
                toolbar={
                    <ActionBar
                        data={headerToolBar}
                        onContextMenuClick={handleHeaderContextClick}
                    />
                }
            />
            <Content>
                <div tabIndex={0} className="resourceManager-content">
                    <FolderTreeView panel={panel} />
                </div>
            </Content>
        </div>
    );
};
