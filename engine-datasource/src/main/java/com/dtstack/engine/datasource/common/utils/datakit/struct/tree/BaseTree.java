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

package com.dtstack.engine.datasource.common.utils.datakit.struct.tree;

import java.util.List;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-04-17 20:36.
 */
public

abstract class BaseTree<K, T extends BaseTree> {

    private List<T> children;

    /**
     * 获取节点的id
     *
     * @return 本节点的id
     */
    public abstract K getId();

    /**
     * 获取节点的所属父id,若无父id则返回0
     *
     * @return 父节点的id
     */
    public abstract K getParentId();

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
