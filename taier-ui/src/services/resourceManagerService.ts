/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { FolderTreeService } from '@dtinsight/molecule/esm/services';

let resourceManagerTree;
class ResourceManagerTree extends FolderTreeService {
    /**
     * 判断该节点是否为 file
     * @description 资源管理的所有节点，文件夹节点的 id 为 `xxx-folder`，只有文件节点的 id 是可以直接通过服务端接口数据的 id 获取数据
     * @link {catalogueService#L90-106}
     */
    checkNotDir = (id: number | string) => {
        if (typeof id === 'number') {
            const node = this.get(id);
            return node ? Promise.resolve() : Promise.reject(new Error('请选择具体文件, 而非文件夹'));
        }

        return Promise.reject(new Error('请选择具体文件, 而非文件夹'));
    };
}

if (!resourceManagerTree) {
    resourceManagerTree = new ResourceManagerTree();
}

export default resourceManagerTree as ResourceManagerTree;
