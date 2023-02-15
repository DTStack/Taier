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

import './index.scss';

class ProgressBar {
    private clock: number | null;
    private count: number;
    private className: string;
    private hodor: HTMLDivElement;

    constructor() {
        this.clock = null;
        this.count = 0;
        this.className = 'dtc-progress-progress-bar';
        this.hodor = document.createElement('div');
        this.hodor.className = this.className;
    }

    show() {
        this.count += 1;
        if (!this.hasAdded() && !this.clock) {
            this.clock = window.setTimeout(() => {
                document.body.appendChild(this.hodor);
            }, 200);
        }
    }

    hide() {
        this.count -= 1;
        if (this.count <= 0) {
            if (this.clock) {
                clearTimeout(this.clock);
                this.clock = null;
            }
            if (this.hasAdded()) {
                this.hodor.remove();
            }
        }
    }

    hasAdded() {
        return document.getElementsByClassName(this.className).length > 0;
    }
}
export default new ProgressBar();
