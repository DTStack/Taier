import 'whatwg-fetch'
import { reqHeader, authBeforeRes } from './interceptor';
import ProgressBar from '../components/progressBar/index';
import token from './token';

const AUTH_PARAM = 'Authorization';

class Http {
  get(url, params) { 
    let options = { method: 'GET' }
    let req_url = params ? this.buildUrl(url, params) : url;
    return this.request(req_url, options)
  }

  post(url, data) {
    let options = { method: 'POST', headers: { "content-type": "application/json;charset=UTF-8" },body:{} }
    if (data) options.body = JSON.stringify(data)
    return this.request(url, options)
  }

  delete(url, params) { 
    let options = { method: 'DELETE' }
    let req_url = params ? this.buildUrl(url, params) : url;
    return this.request(req_url, options)
  }

  put(url, data) {
    let options = { method: 'PUT',body:{}  }
    if (data) options.body = JSON.stringify(data)
    return this.request(url, options)
  }

  postForm(url, data, flag) {
    let options = { method: 'POST',body:{}  }
    if (data) options.body = flag ? this.buildFormData(data) : new FormData(data);
    return this.request(url, options)
  }
  head(url) {
    let options = { method: 'Head' }
    return this.request(url, options)
  }
  buildUrl(url, params) {
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
  request(url, options) {
    // console.log(token.get());
    const header = Object.assign(reqHeader, { [AUTH_PARAM]: token.get() })
    // console.log(options.headers)
    options.headers = header;
    options.credentials = 'same-origin'
    ProgressBar.show();
    return fetch(url, options)
      .then(authBeforeRes)
      .then(response => {
        ProgressBar.hide();
        return response.json()
      })
      .catch(err => {
        console.error("错误信息：",JSON.stringify(err));
        this.handleExcept(err);//开发环境可讲此方法注视
      });
  }
  handleExcept(e){
    console.log(e)
    const status = e.name;
    console.log(status);
    if (status === 401) {
     window.location.href='/login';
      return;
    }
    if (status === 403) {
      window.location.href='/login';
      return;
    }
    if (status <= 504 && status >= 500) {
      window.location.href='/login';
      return;
    }
    if (status >= 404 && status < 422) {
      window.location.href='/login';
    }
  }
}
export default new Http()
