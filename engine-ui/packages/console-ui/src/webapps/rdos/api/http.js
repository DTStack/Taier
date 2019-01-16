import 'whatwg-fetch'

import ProgressBar from 'widgets/progress-bar';
import { authAfterFormated, authBeforeFormate } from '../interceptor';

class Http {
/* eslint-disable */
    get(url, params) { // GET请求
        const newUrl = params ? this.build(url, params) : url
        return this.request(newUrl, {
            method: 'GET',
        })
    }

    post(url, body) { // POST请求
        let options = { method: 'POST' }
        if (body) options.body = JSON.stringify(body)
        return this.request(url, options)
    }

    postAsFormData(url, params) {
        let options = { method: 'POST' }
        if (params) options.body = this.buildFormData(params)
        return this.request(url, options)
    }

    postForm(url, form) {
        let options = { method: 'POST' }
        if (form) options.body = new FormData(form)
        return this.request(url, options)
    }

    request(url, options) {
        ProgressBar.show()
        options.credentials = 'same-origin'
        return fetch(url, options)
        .then(authBeforeFormate)
        .then(response => {
            setTimeout(() => {
                ProgressBar.hide()
            }, 300);
            return response.json()
        })
        .then(authAfterFormated)
        .catch( err => {
            ProgressBar.hide()
            console.log(err);
            return err;
        });
    }

    defaultHeader() { // 默认头
        const header = {
            'Accept': '*/*',
            'Content-Type': 'application/json',
        };
        return header
    }

    build(url, params) { // URL构建方法
        const ps = []
        if (params) {
            for (let p in params) {
                if (p) {
                    ps.push(p + '=' + encodeURIComponent(params[p]));
                }
            }
        }
        return url + '?' + ps.join('&')
    }

    buildFormData(params) {
        if (params) {
            const data = new FormData()
            for (let p in params) {
                if (p) {
                    data.append(p, params[p])
                }
            }
            return data;
        }
    }
}
/* eslint-disable */
export default new Http()
