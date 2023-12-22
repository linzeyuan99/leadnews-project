import instance from "../utils/request";
import axios from "axios";
import qs from 'qs'

export const $notification = async (params) => {
    let token = localStorage.getItem('token')
    let { data } = await axios.get('http://127.0.0.1:51601/apiArticle/lin/article/notification?authorId=' + params, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $addLike = async (params) => {
    console.log('params', params);
    let token = localStorage.getItem('token')
    console.log('token', token);
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/like?idStr=' + params + '&userId=' + localStorage.getItem('id'), {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $addColletion = async (params) => {
    console.log('params', params);
    let token = localStorage.getItem('token')
    console.log('token', token)
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/collect?idStr=' + params + '&userId=' + localStorage.getItem('id'), {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $comment = async (params) => {
    debugger
    let token = localStorage.getItem('token')
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/comment', params, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $commentList = async (params) => {
    debugger
    let token = localStorage.getItem('token')
    let { data } = await axios.get('http://127.0.0.1:51601/apiArticle/lin/article/commentList?idStr=' + params, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}

export const $deleteComment = async (params) => {
    debugger
    let token = localStorage.getItem('token')
    let { data } = await axios.post('http://127.0.0.1:51601/apiArticle/lin/article/delete?id=' + params, {
        headers: {
            'token': `${token}`
        }
    })
    return data;
}


export const $browseList = async (params) => {
    debugger
    let token = localStorage.getItem('token')
    let { data } = await axios.get('http://127.0.0.1:51601/apiArticle/lin/article/browseList', {
        headers: {
            'token': `${token}`
        }
    })
    return data;
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
    console.log('error', error);
    console.log('error222--status', error.response.status);
    if (error.response.status == '401') {
        window.location.href = '/'//跳转 到首页
    }
    return Promise.reject(error);
});