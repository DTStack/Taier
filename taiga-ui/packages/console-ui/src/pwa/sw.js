var cacheWhitelist = ['DTinsight'];

// service worker 注册事件
this.addEventListener('install', function (e) {
    // 一般注册以后，激活需要等到再次刷新页面后再激活
    // 可防止出现等待的情况，这意味着服务工作线程在安装完后立即激活。
    self.skipWaiting();
});

// 运行触发的事件。
// 这里将更新缓存策略。
this.addEventListener('activate', function (e) {
    e.waitUntil(
        caches.keys().then(function (keyList) {
            return Promise.all(keyList.map(function (key) {
                if (cacheWhitelist.indexOf(key) === -1) {
                    return caches.delete(key);
                }
            }))
        })
    )
});

// 缓存优先
function firstCache (cacheName, request) {
    // request.mode = 'cors';
    // return caches.open(cacheName).then(function(cache) {
    //     return cache.match(request).then(function(response) {
    //         var fetchServer = function () {
    //             return fetch(request,{
    //                 mode: 'cors',
    //                 credentials: 'omit'
    //             }).then(function(newResponse) {
    //                 // 对比缓存
    //                 if (response && response.status == 200) {
    //                     var oldTime = new Date (response.headers.get('Last-Modified')),
    //                         newTime = new Date (newResponse.headers.get('Last-Modified'));

    //                     // 判断是否缓存是否有问题。
    //                     if (oldTime.valueOf() != newTime.valueOf()) {
    //                         newResponse.clone().blob().then(function (res) {
    //                             postMsg({
    //                                 src : request.url,
    //                                 blob : res
    //                             });
    //                         });
    //                     }
    //                 }

    //                 cache.put(request, newResponse.clone());
    //                 return newResponse;
    //             });
    //         };

    //         if (response && response.status == 200) {
    //             setTimeout(fetchServer, 1000);
    //             return response;
    //         } else {
    //             return fetchServer(true);
    //         }
    //     });
    // })

    return caches.open(cacheName).then(function (cache) {
        return cache.match(request).then(function (response) {
            var fetchServer = function () {
                return fetch(request).then(function (newResponse) {
                    cache.put(request, newResponse.clone());
                    return newResponse;
                });
            };

            if (response) {
                setTimeout(fetchServer, 1000);
                return response;
            } else {
                return fetchServer(true);
            }
        });
    })
}

// 竞速模式
// 网络好的时候优先使用
function networkCacheRace (cacheName, request) {
    var timeId; var TIMEOUT = 500;

    return Promise.race([new Promise(function (resolve, reject) {
        timeId = setTimeout(function () {
            caches.open(cacheName).then(function (cache) {
                cache.match(request).then(function (response) {
                    if (response) {
                        resolve(response);
                    }
                });
            });
        }, TIMEOUT);
    }), fetch(request).then(function (response) {
        clearTimeout(timeId);
        caches.open(cacheName).then(function (cache) {
            cache.put(request, response);
        });
        return response.clone();
    }).catch(function () {
        clearTimeout(timeId);
        return caches.open(cacheName).then(function (cache) {
            return cache.match(request);
        });
    })]);
}

function matchRules (url, rules) {
    var match = false;
    for (var i = 0, reg; !match && (reg = rules[i]); ++i) {
        match = match || (reg.test && reg.test(url));
    }
    return match;
}

// 监听页面的请求。
// 只能缓存get请求。
this.addEventListener('fetch', function (e) {
    var request = e.request;

    var url = request.url;

    var cacheName = cacheWhitelist[0];
    // 页面，js，css等资源网络优先
    // 当500毫秒还没返回就直接使用缓存。
    if (matchRules(url, [/.(js|html|css|txt)(\?|#|$)/i])) {
        e.respondWith(networkCacheRace(cacheName, request));
    } else if (matchRules(url, [/.(png|jpg|jpeg|gif|webp)(\?|#|$)/i])) {
        // 图片缓存优先
        e.respondWith(firstCache(cacheName, request));
    }
});
