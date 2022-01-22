const license = `
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

`

class InsertHTMLPlugin {
    getResource(compilation) {
        const _myAssets = new Map();

        try {
            const assets = compilation.assets;
            const regExp = new RegExp(/.*(?<=js)$/g);

            for (let asset in assets) {
                if (regExp.test(asset)) {
                    let source = assets[asset].source();

                    /**
                     * There may be buffer resource interference
                     */
                    if (typeof source === 'string') {
                        source = license +source;
                        _myAssets.set(asset, {
                            source: function() {
                                return source;
                            },
                            size: function() {
                                return source.length;
                            },
                        });
                    }else{
                        
                    }
                }
            }
            return _myAssets;
      } catch (error) {
            console.error(error, 'Something went wrong with your packaging process, check the HTML');
      }
    }

    apply(compiler) {
        const plugin = 'LicensePlugin';

        compiler.hooks.emit.tap(plugin, compilation => {
            let test = this.getResource(compilation);
            test.forEach((assetItem, assetKey) => {
                compilation.assets[assetKey] = assetItem;
            });
        });
    }
}

module.exports = InsertHTMLPlugin;
