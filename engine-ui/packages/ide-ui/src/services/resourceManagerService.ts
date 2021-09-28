import { FolderTreeService } from 'molecule/esm/services';
let resourceManagerTree;

class ResourceManagerTree extends FolderTreeService {
    constructor() {
        super();
    }
}

if (!resourceManagerTree) {
    resourceManagerTree = new ResourceManagerTree();
}

export default resourceManagerTree as ResourceManagerTree;
