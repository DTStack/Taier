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

/**
 * Copy工具类
 *
 * 使用方法：
 *  import CopyUtils from 'copy.js';
 *  const instance = new CopyUtils();
 *  instance.copy(value, callback);
 */
export default class CopyUtils {
  fakeHandlerCallback: any;
  fakeHandler: any = null;
  fakeElem: any;

  copy(value: any, callback?: Function) {
    this.removeFake();

    this.fakeHandlerCallback = () => this.removeFake();
    this.fakeHandler = document.body.addEventListener(
      'click',
      this.fakeHandlerCallback,
    );

    this.fakeElem = document.createElement('textarea');
    // Prevent zooming on iOS
    this.fakeElem.style.fontSize = '12pt';

    // Reset box model
    this.fakeElem.style.border = '0';
    this.fakeElem.style.padding = '0';
    this.fakeElem.style.margin = '0';

    // Move element out of screen horizontally
    this.fakeElem.style.position = 'absolute';
    this.fakeElem.style.left = '-9999px';

    // Move element to the same position vertically
    let yPosition = window.pageYOffset || document.documentElement.scrollTop;
    this.fakeElem.style.top = `${yPosition}px`;

    this.fakeElem.setAttribute('readonly', '');
    this.fakeElem.value = value;

    document.body.appendChild(this.fakeElem);
    this.fakeElem.select();

    callback && this.copyText(callback);
  }

  removeFake() {
    if (this.fakeHandler) {
      document.body.removeEventListener('click', this.fakeHandlerCallback);
      this.fakeHandler = null;
      this.fakeHandlerCallback = null;
    }

    if (this.fakeElem) {
      document.body.removeChild(this.fakeElem);
      this.fakeElem = null;
    }
  }

  copyText(callback: Function) {
    let succeeded;

    try {
      succeeded = document.execCommand('copy');
    } catch (err) {
      succeeded = false;
    }
    if (callback) callback(succeeded);
  }
}
