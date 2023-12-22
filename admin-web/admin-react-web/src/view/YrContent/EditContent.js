import React, { useState, useRef, useEffect } from "react";
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { Button, DatePicker, Form, Modal, message } from 'antd';
import { $updateArticleApi } from '../../api/addArticalApi'
import {
    ExclamationCircleOutlined
} from '@ant-design/icons';

export default function EditContent() {
    let navigate = useNavigate();
    const local = useLocation();
    const stateVal = local.state
    const res = stateVal.res

    const nowContent = res.content
    const idStr = res.idStr

    const [contents, setConents] = useState(nowContent)
    const [modal, contextHolder] = Modal.useModal();
    const quillRef = useRef(null);

    const config = {
        rules: [
            {
                type: 'object',
                required: true,
                message: '請選擇時間!',
            },
        ],
    };

    const handleContentChange = (val) => {
        setConents(val);
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
                console.log('contents',contents);
                console.log('time',time);
                res.content = contents
                res.publishTimeStr = time
                $updateArticleApi(res).then(data=>{
                    debugger
                    console.log('data',data);
                })
                
                setTimeout(() => {
                    navigate('/layout/yrContent');
                }, 1000);

            }
        });

    }

    return (
        <div>
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
                    value={contents}
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
            {contextHolder}
        </div>
    )
}