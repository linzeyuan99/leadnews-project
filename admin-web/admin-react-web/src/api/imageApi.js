import instance from "../utils/request";
import axios from "axios";
import qs from 'qs'

export const $uploadImg = async (params) =>{
    debugger
    console.log('params:',params);
    let token = localStorage.getItem('token')
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/uploadImg',{file:params},{
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

axios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    console.log('error111',error);
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    console.log('error',error);
    console.log('error222--status',error.response.status);
    if(error.response.status == '401'){
        window.location.href = '/'//跳转 到首页
    }
    return Promise.reject(error);
});