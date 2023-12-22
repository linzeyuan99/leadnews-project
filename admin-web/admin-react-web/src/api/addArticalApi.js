import instance from "../utils/request";
import axios from "axios";
import { useNavigate} from 'react-router-dom';
import qs from 'qs'



export const $addArticalApi = async (params) => {
    let token = localStorage.getItem('token')
    debugger
    console.log('pa', params);

    let pa = { authorName: params.authorName, authorId: params.authorId, content: params.content, publishTimeStr: params.publishTime }

    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/addArtical', pa,{
        headers: {
            'token': `${token}`
        }
    })

    return data;
}

export const $listArticalApi = async (params) => {
    let token = localStorage.getItem('token')

    console.log('params', params);
    console.log('token:is:', token);
    let { data } = await axios.get('http://127.0.0.1:51601/apiArticle/lin/article/listAll?authorId=' + params.authorId, {
        headers: {
            'token': `${token}`
        }
    })
    console.log('list', data);
    return data;
}

export const $delArticalApi = async (params) => {
    let token = localStorage.getItem('token')
    debugger
    console.log('params', params);

    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/deleted?id=' + params,{
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $updateArticleApi = async (params) => {
    let token = localStorage.getItem('token')
    debugger
    console.log('params', params);
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/update', params,{
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
    console.log('error222--status',error.response.status);
    if(error.response.status == '401'){
        window.location.href = '/'//跳转 到首页
    }
    return Promise.reject(error);
});