import React, { useState, useRef, useEffect } from "react";
import { useNavigate, useHistory } from 'react-router-dom';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { Button, DatePicker, Form, Modal, message } from 'antd';
import { $addArticalApi } from '../../api/addArticalApi'
import {
    ExclamationCircleOutlined
} from '@ant-design/icons';


export default function AddArticle() {
    let navigate = useNavigate();//導航要放第一

    const [content, setConent] = useState('')
    const [modal, contextHolder] = Modal.useModal();
    const quillRef = useRef(null);

    let id = localStorage.getItem('id')
    let name = localStorage.getItem('name')

    const handleContentChange = (val) => {
        setConent(val);
    }

    const onFinish = (fieldsValue) => {
        //是否确认方法
        modal.confirm({
            title: '提示',
            icon: <ExclamationCircleOutlined />,
            content: '是否提交',
            okText: '提交',
            cancelText: '取消',
            onOk() {
                const values = {
                    ...fieldsValue,
                    '定時時間': fieldsValue['date-time-picker'].format('YYYY-MM-DD HH:mm:ss')
                };
                let time = fieldsValue['date-time-picker'].format('YYYY-MM-DD HH:mm:ss')
                let s = { content }
                let params = { authorName: name, authorId: id, publishTime: time, content: s.content }
                $addArticalApi(params).then(data => {
                    console.log('data', data)
                    afterSubmit(data)
                    setConent('')
                })
            }
        });

    };

    const afterSubmit = (res) => {
        if (res.code == '200') {
            modal.confirm({
                title: '提示',
                icon: <ExclamationCircleOutlined />,
                content: '操作成功,待審核',
                okText: '確認',
                cancelText: '取消',
                onOk() {
                    //跳轉頁面
                    setTimeout(() => {
                        navigate('/layout/home');
                    }, 1000);
                }
            })
        } else {
            modal.confirm({
                title: '提示',
                icon: <ExclamationCircleOutlined />,
                content: '操作失败,请稍后再试',
                okText: '確認',
                cancelText: '取消'
            })
        }
    }


    const config = {
        rules: [
            {
                type: 'object',
                required: true,
                message: '請選擇時間!',
            },
        ],
    };


    return (
        <div>
            {contextHolder}
            <Form
                name="time_related_controls"
                onFinish={onFinish}
                style={{
                    maxWidth: 600,
                }}
            >
                <ReactQuill
                    ref={quillRef}
                    className="publish-quill"
                    theme="snow"
                    placeholder="請輸入文章内容"
                    style={{ width: 1200, height: 500 }}
                    onChange={handleContentChange}

                />

                <Form.Item name="date-time-picker" label="定時發佈時間" {...config} style={{ marginTop: 50 }}>
                    <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" />
                </Form.Item>

                <Form.Item
                    wrapperCol={{
                        xs: {
                            span: 24,
                            offset: 0,
                        },
                        sm: {
                            span: 16,
                            offset: 8,
                        },
                    }}
                >

                    <Button type="primary" htmlType="submit" style={{ marginTop: 20, marginLeft: -200 }}>
                        提交
                    </Button>
                </Form.Item>
            </Form>
        </div>
    )
}