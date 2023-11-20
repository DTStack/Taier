import type molecule from '@dtinsight/molecule';
import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IBreadcrumbItemProps } from '@dtinsight/molecule/esm/components';

import { CATALOGUE_TYPE } from '@/constant';
import { catalogueService } from '.';

interface IBreadcrumbService {
    /**
     * 根据目录树 id 获取父节点并生成面包屑
     */
    getBreadcrumb: (id: UniqueId) => IBreadcrumbItemProps[];
}

export default class BreadcrumbService implements IBreadcrumbService {
    private hashTree: TreeViewUtil<molecule.model.IFolderTreeNodeProps> | null = null;
    constructor() {
        catalogueService.onUpdate(() => {
            this.updateTree();
        });
    }

    private updateTree = () => {
        const root = catalogueService.getRootFolder(CATALOGUE_TYPE.TASK);
        if (root) {
            this.hashTree = new TreeViewUtil<molecule.model.IFolderTreeNodeProps>(root);
        }
    };

    public getBreadcrumb = (id: UniqueId) => {
        if (this.hashTree) {
            const stack = [this.hashTree.getHashMap(id)];
            const res: IBreadcrumbItemProps[] = [];
            while (stack.length) {
                const hash = stack.pop();
                if (hash) {
                    res.push({
                        id: hash.node.id,
                        name: hash.node.name,
                    });

                    if (hash.parent) {
                        stack.push(this.hashTree.getHashMap(hash.parent));
                    }
                }
            }

            return res.reverse();
        }
        return [];
    };
}
