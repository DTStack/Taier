import { FolderTreeService } from '@dtinsight/molecule/esm/services';

let functionManagerService;
class FunctionManagerService extends FolderTreeService {
    constructor() {
        super();
    }
}

functionManagerService = functionManagerService || new FunctionManagerService();

export default functionManagerService as FunctionManagerService;
