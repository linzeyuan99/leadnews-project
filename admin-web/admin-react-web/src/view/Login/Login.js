import React, { useEffect } from "react";
import { useNavigate, useHistory } from 'react-router-dom'
import '../../css/Login.scss';
import { Button, Form, Input, message, Watermark, Tooltip } from 'antd';
import { $login } from '../../api/userApi'

export default function Login() {
    let navigate = useNavigate();//導航要放第一
    const [messageApi, contextHolder] = message.useMessage();
    useEffect(() => {
        //判斷是否登錄成功
        if (sessionStorage.getItem('token')) {
            navigate('/layout')
        }
    }, [])

    let [form] = Form.useForm();

    const onFinish = (values) => {
        $login(values).then(data => {
            let msg = data.resMsg
            let resData = data.data
            console.log('data',data);
            if (data.code == '200') {
                console.log('登录')
                onSubmit(resData)
            } else {
                messageApi.open({
                    type: 'error',
                    content: msg
                })
            }
        })
    };

    const onSubmit = (val) => {
        localStorage.setItem('id', val.user.id)
        localStorage.setItem('name', val.user.name)
        localStorage.setItem('phone',val.user.phone)
        console.log('resData.token', val.token);
        console.log('val',val);
        // sessionStorage.setItem('token',val.token)
        localStorage.setItem('token', val.token)
        if (val.auto_tag == 'AUTO_LOGIN') {
            messageApi.open({
                type: 'success',
                content: '注册并登录成功！',
            });
        } else {
            messageApi.open({
                type: 'success',
                content: '登錄成功！',
            });
        }




        //跳轉頁面
        setTimeout(() => {
            navigate('/layout');
        }, 1000);
    };

    return (
        <Watermark content="LUCKY">
            <div className="login">
                <div className="content">
                    <h2>文章娛樂~~請登錄~~</h2>
                    <Form
                        name="basic"
                        form={form}
                        labelCol={{
                            span: 4,
                        }}
                        wrapperCol={{
                            span: 18,
                        }}
                        style={{
                            maxWidth: 600,
                        }}
                        initialValues={{
                            username: '',
                            password: ''
                        }}
                        onFinish={onFinish}
                        autoComplete="off"
                    >
                        <Form.Item
                            label="賬號"
                            name="name"
                            rules={[
                                {
                                    required: true,
                                    message: '請輸入你的賬號！',
                                },
                            ]}
                        >
                            <Input />
                        </Form.Item>

                        <Form.Item
                            label="密碼"
                            name="password"
                            rules={[
                                {
                                    required: true,
                                    message: '請輸入你的密碼！',
                                },
                            ]}
                        >
                            <Input.Password />
                        </Form.Item>

                        <Form.Item
                            wrapperCol={{
                                offset: 4,
                                span: 16,
                            }}
                        >

                            <Button type="primary" htmlType="submit" style={{ marginLeft: '10px' }}>
                                登錄
                            </Button>
                            <Button style={{ marginLeft: '10px' }} onClick={() => { form.setFieldValue() }} >
                                取消
                            </Button>

                            <Tooltip title="未注冊用戶登錄會自動注冊！">
                                <span style={{ marginLeft: '10px' }}>提示</span>
                            </Tooltip>

                        </Form.Item>
                    </Form>
                </div>
                {contextHolder}
            </div>
        </Watermark>
    )
}