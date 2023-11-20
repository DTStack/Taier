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

import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { history } from 'umi';

import { DRAWER_MENU_ENUM } from '@/constant';

function handleMenuBarEvents() {
    molecule.menuBar.onSelect((menuId) => {
        switch (menuId) {
            case DRAWER_MENU_ENUM.TASK:
            case DRAWER_MENU_ENUM.STREAM_TASK:
            case DRAWER_MENU_ENUM.SCHEDULE:
            case DRAWER_MENU_ENUM.PATCH:
            case DRAWER_MENU_ENUM.QUEUE:
            case DRAWER_MENU_ENUM.RESOURCE:
            case DRAWER_MENU_ENUM.CLUSTER:
                history.push({
                    query: {
                        drawer: menuId,
                    },
                });
                break;
            case 'Open': {
                molecule.extension.executeCommand('quickOpen');
                break;
            }
            case 'About': {
                window.open('https://github.com/DTStack/Taier');
                break;
            }
            default:
                break;
        }
    });
}

/**
 * This is for adding menu data modules
 */
export default class MenuExtension implements IExtension {
    id: UniqueId = 'menu';
    name = 'menu';
    activate(): void {
        handleMenuBarEvents();
    }
    dispose(): void {
        throw new Error('Method not implemented.');
    }
}
