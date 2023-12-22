import React from "react";
import { Button, Form, Input, message } from 'antd';
import { $getCode, $bindPhoneNum } from '../../api/userApi'


export default function YrPhoneNum() {
    var phone = localStorage.getItem('phone')
    var id = localStorage.getItem('id')

    const [messageApi, contextHolder] = message.useMessage();
    let [form] = Form.useForm();
    console.log('form', form);
    const havePhone = (phone) => {
        debugger
        let isPhone = false
        if (phone == '' || phone == null || phone == 'null') {
            isPhone = true
        }
        return isPhone
    }

    function isValidPhoneNumber(phone) {

        const hongKongRegex = /^\d{8}$/;
        const mainlandRegex = /^\d{11}$/;
        if (hongKongRegex.test(phone)) {
            return '你已注冊手機號碼--------中國香港:' + phone
        } else if (mainlandRegex.test(phone)) {
            return '你已注冊手機號碼--------中國大陸:' + phone
        }
    }

    function isValidPhoneNumber02(phone) {
        debugger
        const hongKongRegex = /^\d{8}$/;
        const mainlandRegex = /^\d{11}$/;
        if (phone.length == 8) {
            if (hongKongRegex.test(phone)) {
                return true
            }
        } else if (phone.length == 11) {
            if (mainlandRegex.test(phone)) {
                return true
            }
        } else {
            alert("請輸入正確的手機號碼！")
            return false
        }
    }

    const onFinish = (values) => {
        let pa = { id: id, phone: values.yPhone, code: values.checkCode }

        $bindPhoneNum(pa).then(data => {
            console.log('data2222', data);
            if (data.code == '200') {
                messageApi.open({
                    type: 'success',
                    content: '賬號成功綁定手機號！可用于找回密碼！',
                });
                let phone = data.data
                localStorage.setItem('phone',phone)
                window.location.href = '/layout'
            } else {
                messageApi.open({
                    type: 'error',
                    content: data.resMsg,
                });
            }

        })
    };

    const getCode = () => {
        let val = form.getFieldsValue('yPhone')
        let phone = val.yPhone
        let isFlag = isValidPhoneNumber02(phone)
        if (isFlag) {
            $getCode().then(data => {
                if (data.code == '200') {
                    messageApi.open({
                        type: 'success',
                        content: data.data
                    })
                } else {
                    messageApi.open({
                        type: 'error',
                        content: data.data
                    })
                }
            })
        }
    }

    return (
        <>
            {havePhone(phone) ? (
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
                        initialValues={{
                            yPhone: ''
                        }}
                        onFinish={onFinish}
                        autoComplete="off"
                    >
                        <Form.Item
                            label="手機號碼"
                            name="yPhone"
                            rules={[
                                {
                                    required: true,
                                    message: '請輸入您的手機號碼！',
                                },
                            ]}
                        >
                            <Input />
                        </Form.Item>

                        <Form.Item
                            label="驗證碼"
                            name="checkCode"
                            rules={[
                                {
                                    required: true,
                                    message: '請輸入您的驗證碼！',
                                },
                            ]}
                        >
                            <Input />


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
                            <Button onClick={getCode} style={{ marginLeft: 10 }}>
                                發送驗證碼
                            </Button>
                        </Form.Item>

                    </Form>

                    {contextHolder}
                </div>

            ) : (
                <div>{isValidPhoneNumber(phone)}</div>
            )}
        </>

    );
};
