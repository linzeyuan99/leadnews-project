import React, { useEffect, useState } from "react";
import { $listArticalApi, $delArticalApi } from "../../api/addArticalApi";
import { Button, Table, Tag, message, Modal } from 'antd';
import { useNavigate} from 'react-router-dom';
import {
    ExclamationCircleOutlined
} from '@ant-design/icons';

export default function YrContent() {
    let navigate = useNavigate();//導航要放第一
    const [modal, contextHolder1] = Modal.useModal();
    const [messageApi, contextHolder] = message.useMessage();
    let id = localStorage.getItem('id')
    const [myList, setMyList] = useState([])

    useEffect(() => {
        let pa = { authorId: id }
        $listArticalApi(pa).then(data => {
            let res = data.data
            setMyList(res)
        })
    }, [])

    const del = (parId) => {
        //是否确认方法
        modal.confirm({
            title: '提示',
            icon: <ExclamationCircleOutlined />,
            content: '是否刪除',
            okText: '提交',
            cancelText: '取消',
            onOk() {
                console.log('parId', parId)
                $delArticalApi(parId).then(data => {
                    console.log(data);
                    if (data.code == '200') {
                        messageApi.open({
                            type: 'success',
                            content: '刪除成功！',
                        });

                        let pa = { authorId: id };
                        $listArticalApi(pa).then(data => {
                            let res = data.data;
                            setMyList(res);
                        });
                    }

                })
            }
        });
    }

    const edit = (ret) =>{
        console.log('ret',ret)
        navigate('/layout/yrEditContent',{state:{res:ret}})
    }

    const columns = [
        {
            title: '作者名字',
            dataIndex: 'authorName',
            key: 'authorName'
        },
        {
            title: '文章内容',
            dataIndex: 'content',
            key: 'content',
        },
        {
            title: '文章狀態',
            key: 'isSuccess',
            dataIndex: 'isSuccess',
            render: (isSuccess) => (
                <>
                    {isSuccess == 1 ? <Tag>已審核</Tag> : <Tag>未審核</Tag>}
                </>
            ),
        },
        {
            title: '創建時間',
            dataIndex: 'publishTimeStr',
            key: 'publishTimeStr',
        },
        {
            title: '操作',
            key: 'edit',
            render: (ret) => (
                <>
                    <Button size="small" onClick={() => { edit(ret) }} >编辑</Button>
                    <Button danger size="small" style={{marginLeft:10}} onClick={() => { del(ret.idStr) }} >刪除</Button>
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