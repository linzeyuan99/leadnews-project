import axios from 'axios';
import { baseURL } from '../config';


var instance = axios.create({
  baseURL: baseURL,
  // timeout: 20000
});


instance.interceptors.request.use(function (config) {

  return config;
}, function (error) {

  return Promise.reject(error);
});


instance.interceptors.response.use(function (response) {

  return response;
}, function (error) {
  console.log('error222--status', error.response.status);
  if (error.response.status == '401') {
    window.location.href = '/'//跳转 到首页
  }
  return Promise.reject(error);
});

export default instance;