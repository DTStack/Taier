import { createContext } from 'react';

interface IClusterContext {
    /**
     * 组件连通性，当失败了，用 string 存储失败信息
     */
    connectable: Record<string, true | string>;
    setConnectable: React.Dispatch<React.SetStateAction<IClusterContext['connectable']>>;
    /**
     * 组件编辑状态
     */
    editedComponents: Record<string, boolean>;
    setEdited: React.Dispatch<React.SetStateAction<IClusterContext['editedComponents']>>;
    principals: string[];
    setPrincipals: React.Dispatch<React.SetStateAction<IClusterContext['principals']>>;
}

export default createContext<IClusterContext>({
    connectable: {},
    setConnectable: () => {},
    editedComponents: {},
    setEdited: () => {},
    principals: [],
    setPrincipals: () => {},
});
