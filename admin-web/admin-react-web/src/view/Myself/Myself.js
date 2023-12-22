import React, { useState, useEffect } from "react";
import { Button, Form, Input } from 'antd';
import { $proList } from '../../api/userApi'

const Myself = () => {
    let [form] = Form.useForm();
    let lid = localStorage.getItem('id')
    const [formData, setFormData] = useState({

    });

    useEffect(() => {
        $proList(lid).then(data => {
            let list = data.data
            let res = list[0]
            debugger
            console.log('ku', res)
            setFormData(res)
        })
    }, [lid])

    const onFinish = (values) => {
    };

    return (
        <div>
            <Form
                name="basic"
                form={form}
                labelCol={{
                    span: 8,
                }}
                wrapperCol={{
                    span: 16,
                }}
                style={{
                    maxWidth: 600,
                }}
                initialValues={formData}
                onFinish={onFinish}

            >
                <Form.Item
                    label="用戶名"
                    name="name"
                >
                    {formData.name}
                </Form.Item>

                <Form.Item
                    label="手機號碼"
                    name="phone"
                    rules={[
                        {
                            message: '請輸入您的手機號碼!'
                        },
                    ]}
                >
                 {formData.phone || '暫未填寫' }   
                </Form.Item>

                <Form.Item
                    label="注冊時間"
                    name="createTime"
                >
                {formData.createdTime}
                </Form.Item>

                <Form.Item
                    label="個人簡介"
                    name="profile"
                    rules={[
                        {
                            message: '請輸入您的簡介!'
                        },
                    ]}
                >
                 {formData.profile || '暫未填寫' }  
                </Form.Item>

                <Form.Item
                    wrapperCol={{
                        offset: 4,
                        span: 16,
                    }}
                >
                    {/* <Button type="primary" htmlType="submit">
                        提交
                    </Button> */}
                </Form.Item>
            </Form>
        </div>
    );
};


export default Myself;