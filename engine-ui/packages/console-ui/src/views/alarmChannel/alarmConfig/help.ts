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

import { isEqual } from 'lodash'
import { handleComponentConfigAndCustom, handleComponentTemplate,
    handleComponentConfig } from '../../clusterManage/newEdit/help'

export const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    }
}

export function getConfig (comp: any, preComp: any): any {
    const { componentTypeCode } = preComp
    return handleComponentConfigAndCustom(comp, componentTypeCode)
}

export function getTemplate (comp: any, preComp: any): any {
    return handleComponentTemplate(comp, preComp)
}

export function getInitailConfig (comp: any): any {
    return handleComponentConfig({
        componentConfig: JSON.parse(comp.componentConfig)
    }, true)
}

export function validateConfig (comp: any, preComp: any): boolean {
    const { componentConfig } = preComp
    const wrrapConfig = getConfig(comp, preComp)
    if (!isEqual(wrrapConfig, JSON.parse(componentConfig))) return false
    return true
}
