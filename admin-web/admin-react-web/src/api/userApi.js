import instance from "../utils/request";
import axios from "axios";
import qs from 'qs'

//登錄  
export const $login = async (params) => {
    let token = localStorage.getItem('token')
    debugger
    let { data } = await instance.post('http://127.0.0.1:51601/apiUser/lin/user/login',
        { name: params.name, password: params.password }, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

//修改密碼 
export const $updatePwd = async (params) => {
    let userId = localStorage.getItem('id')
    console.log('params', params);
    let token = localStorage.getItem('token')
    let { data } = await axios.post('http://127.0.0.1:51601/apiUser/lin/user/updatePwd?userId=' + userId + '&oldPwd=' + params.oldPwd + '&newPwd=' + params.newPwd, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

//獲取驗證碼
export const $getCode = async (params) => {
    let userId = localStorage.getItem('id')
    console.log('params', params);
    let token = localStorage.getItem('token')
    let { data } = await axios.get('http://127.0.0.1:51601/apiUser/lin/user/getCode?userId=' + userId, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

//個人中心 http://192.168.1.6:51801/lin/user/list
export const $proList = async (lId) => {
    let token = localStorage.getItem('token')
    let { data } = await instance.get('http://127.0.0.1:51601/apiUser/lin/user/list?id=' + lId, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

//綁定 
export const $bindPhoneNum = async (params) => {
    console.log('pa',params);
    let token = localStorage.getItem('token')
    let { data } = await instance.post('http://127.0.0.1:51601/apiUser/lin/user/bindPhoneNum',params, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

//退出登錄 
export const $logout = async (params) => {
    let { data } = await instance.get('http://127.0.0.1:51601/apiUser/lin/user/logout', { params })
    console.log(data)
}




axios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    console.log('error111', error);
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    console.log('error222--status', error.response.status);
    if (error.response.status == '401') {
        window.location.href = '/'//跳转 到首页
    }
    return Promise.reject(error);
});