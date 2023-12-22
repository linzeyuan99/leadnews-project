import React, { useEffect,useState } from "react";
import { Table } from 'antd';
import { $notification } from '../../api/contentApi';

export default function YrNotification() {
    const authorId = localStorage.getItem('id')
    let [mydata,setMyData] = useState('')

    useEffect(() => {
        $notification(authorId).then(data=>{
            let resData = data.data
            console.log("data:",resData);
            setMyData(resData)
        })
    },[])


    const columns = [
        {
            title: '通知管理',
            dataIndex: 'noti',
            key:'noti'
        }
    ];

    const myList = [
        "您的文章内容為：<p>2222</p......已經通過審核~",
        "您的文章内容為：<p>2222212......已經通過审核~",
        "您的文章内容為：<p>asdasda......已經通過审核~"
    ]


    //dataSource={myList}
    const App = () => <Table columns={columns} dataSource={mydata}/>;
    return (
        <App />
    )
}