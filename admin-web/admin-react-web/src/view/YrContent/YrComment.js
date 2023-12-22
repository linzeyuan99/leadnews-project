import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from 'react-router-dom';
import { Table, Button, message, Modal } from 'antd';
import { $commentList, $deleteComment } from '../../api/contentApi'
import {
    ExclamationCircleOutlined
} from '@ant-design/icons';

export default function YrComment() {
    const local = useLocation();
    const r = local.state.res //傳遞過來的 r 
    var idStr = r.idStr // articleId
    var userId = localStorage.getItem('id')
    var userName = localStorage.getItem('name')


    const [modal, contextHolder1] = Modal.useModal();
    const [messageApi, contextHolder] = message.useMessage();

    const del = (res) => {
        //是否确认方法
        console.log('res.id', res.id);
        modal.confirm({
            title: '提示',
            icon: <ExclamationCircleOutlined />,
            content: '是否刪除',
            okText: '提交',
            cancelText: '取消',
            onOk() {
                $deleteComment(res.id).then(data => {
                    console.log(data);
                    if (data.code == '200') {
                        messageApi.open({
                            type: 'success',
                            content: '刪除成功！',
                        });
                        //刷新頁面
                        setTimeout(() => {
                            $commentList(idStr).then(data => {
                                setMyList(data.data)
                            })
                        }, 1000);
                    } else {
                        messageApi.open({
                            type: 'error',
                            content: '刪除失敗！',
                        });
                    }
                })
            }
        });

    }

    useEffect(() => {
        $commentList(idStr).then(data => {
            setMyList(data.data)
        })
    }, [])

    const [myList, setMyList] = useState([])

    const columns = [
        {
            title: '評論人',
            dataIndex: 'commentName',
            key: 'commentName'
        },
        {
            title: '評論内容',
            dataIndex: 'comment',
            key: 'comment',
        },
        {
            title: '評論時間',
            dataIndex: 'createdTimeStr',
            key: 'createdTimeStr',
        },
        {
            title: '刪除',
            key: 'del',
            render: (ret) => (
                <>
                    {ret.commentId == userId ?
                        <Button danger size="small" style={{ marginLeft: 10 }} onClick={() => { del(ret) }} >刪除</Button>
                        : ''}
                </>

            ),
        },
    ];


    const App = () => <Table columns={columns} dataSource={myList} />;
    return (
        <>
            <App />
            {contextHolder}
            {contextHolder1}
        </>
    )
}