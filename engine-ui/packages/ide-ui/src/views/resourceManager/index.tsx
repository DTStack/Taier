import React from 'react';
import { Content, Header } from 'molecule/esm/workbench/sidebar';
import { FolderTree } from 'molecule/esm/workbench/sidebar/explore/index';
import {
    ActionBar,
    IMenuItemProps,
    ITreeNodeItemProps,
} from 'molecule/esm/components';
import molecule from 'molecule/esm';
import './index.scss';
import {
    COMMON_CONTEXT_MENU,
    FileTypes,
    TreeNodeModel,
} from 'molecule/esm/model';
import { LoadEventData } from 'molecule/esm/controller';
import { connect } from 'molecule/esm/react';
import resourceManagerTree from '../../services/resourceManagerService';
import ResModal from './resModal';
import ResViewModal from './resViewModal';
import ajax from '../../api';
import { convertToTreeNode } from '../common/utils';

const FolderTreeView = connect(resourceManagerTree, FolderTree);

interface IResourceProps {
    panel: any;
    headerToolBar: any[];

    onUpload?: () => void;
    onReplace?: () => void;
    onCreateFolder?: () => void;
}

export default ({ panel, headerToolBar, onCreateFolder }: IResourceProps) => {
    const [isModalShow, setModalShow] = React.useState(false);
    const [isViewModalShow, setViewModalShow] = React.useState(false);
    const [resId, setResId] = React.useState(null);
    const [isCoverUpload, setCoverUpload] = React.useState(false);

    const handleUpload = () => {
        setModalShow(true);
        setCoverUpload(false);
    };

    const handleReplace = () => {
        setModalShow(true);
        setCoverUpload(true);
    };

    const toggleUploadModal = () => {
        setModalShow(false);
    };

    const handleHeaderContextClick = (
        e: React.MouseEvent<Element, MouseEvent>,
        item?: IMenuItemProps
    ) => {
        e.preventDefault();
        switch (item?.id) {
            case 'upload':
                handleUpload();
                break;
            case 'replace':
                handleReplace();
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

    const loadData = async (treeNode: LoadEventData) => {
        const res = await ajax.getOfflineCatalogue({
            isGetFile: !!1,
            nodePid: treeNode.data!.id,
            catalogueType: treeNode.data!.data.catalogueType,
            taskType: 1,
            appointProjectId: 1,
            projectId: 1,
            userId: 1,
        });
        if (res.code === 1) {
            const { id, name, children } = res.data;
            const nextNode = new TreeNodeModel({
                id,
                name: name || '资源管理',
                location: name,
                fileType: FileTypes.Folder,
                isLeaf: false,
                data: res.data,
                children: convertToTreeNode(children),
            });

            molecule.folderTree.update(nextNode);
        }
    };

    const handleSelect = (file: ITreeNodeItemProps) => {
        if (file.isLeaf) {
            console.log('file:', file);
            setViewModalShow(true);
            setResId(file.data.id);
        }
    };

    const handleCloseViewModal = () => {
        setViewModalShow(false);
        setResId(null);
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
                    <FolderTreeView
                        onRightClick={handleRightClick}
                        draggable={false}
                        onSelectFile={handleSelect}
                        onLoadData={loadData}
                        panel={panel}
                    />
                </div>
            </Content>
            <ResModal
                isModalShow={isModalShow}
                isCoverUpload={isCoverUpload}
                toggleUploadModal={toggleUploadModal}
            />
            <ResViewModal
                visible={isViewModalShow && resId}
                resId={resId}
                closeModal={handleCloseViewModal}
            />
        </div>
    );
};
