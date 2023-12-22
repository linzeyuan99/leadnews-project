import React from "react";
import { Button, Form, Input, message } from 'antd';
import { $updatePwd } from '../../api/userApi'
import { useNavigate } from "react-router-dom";

export default function ChangePwd() {
    const navigate = useNavigate();
    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = (values) => {
        console.log('Success:', values);
        if (values.newPwd !== values.repeatPwd) {
            alert('兩次輸入新密碼請保持一致！');
        }

        let params = { oldPwd: values.oldPwd, newPwd: values.newPwd }
        $updatePwd(params).then(data => {
            console.log('data', data);
            if (data.code == '200') {
                messageApi.open({
                    type: 'success',
                    content: '修改成功!請重新登陸!',
                });

                setTimeout(() => {
                    navigate('/');
                }, 1500);
            } else {
                messageApi.open({
                    type: 'error',
                    content: data.resMsg,
                });
            }
        })

    };



    return (
        <div>
            {contextHolder}
            <Form
                name="basic"
                labelCol={{
                    span: 8,
                }}
                wrapperCol={{
                    span: 16,
                }}
                style={{
                    maxWidth: 600,
                }}
                initialValues={{
                    oldPwd: '',
                    newPwd: '',
                    repeatPwd: ''
                }}
                onFinish={onFinish}

                autoComplete="off"
            >
                <Form.Item
                    label="舊的密碼"
                    name="oldPwd"
                    rules={[
                        {
                            required: true,
                            message: '請輸入舊的密碼!',
                        },
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="新的密碼"
                    name="newPwd"
                    rules={[
                        {
                            required: true,
                            message: '請輸入新的密碼!',
                        },
                    ]}
                >
                    <Input.Password />
                </Form.Item>

                <Form.Item
                    label="重複新的密碼"
                    name="repeatPwd"
                    rules={[
                        {
                            required: true,
                            message: '請再次輸入新的密碼!',
                        },
                    ]}
                >
                    <Input.Password />
                </Form.Item>

                <Form.Item
                    wrapperCol={{
                        offset: 8,
                        span: 16,
                    }}
                >
                    <Button type="primary" htmlType="submit">
                        確認
                    </Button>
                </Form.Item>
            </Form>
        </div>
    )
}