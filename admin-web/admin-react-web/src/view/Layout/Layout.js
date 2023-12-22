import React, { useState,useEffect } from "react";
import {
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    SlidersOutlined,
    VideoCameraOutlined,
    MailOutlined,
    SettingOutlined,
    NotificationOutlined,
    HomeOutlined,
    FileAddOutlined,
    AlignLeftOutlined,
    ExclamationCircleOutlined,
    AlignRightOutlined

} from '@ant-design/icons';
import { Layout, Menu, Button, theme, Modal, message } from 'antd';
import '../../css/Layout.scss';
import { useNavigate,Outlet } from "react-router-dom";



export default function () {
    const navigate = useNavigate();
    let messageApi = message.useMessage();
    useEffect(() => {
        //判斷是否登錄成功
        if(!sessionStorage.getItem('token')){
            navigate('/layout')
        }
    },[])

    const { Header, Sider, Content } = Layout;
    const [current, setCurrent] = useState('mail');
    const [collapsed, setCollapsed] = useState(false);
    const {
        token: { colorBgContainer },
    } = theme.useToken();
    const [modal, contextHolder] = Modal.useModal();

    //菜單項
    const items = [
        {
            label: '首頁',
            key: 'home',
            icon: <HomeOutlined />,
        },
        {
            label: '手機',
            key: 'phone',
            icon: <MailOutlined />,
        },
        {
            label: '個人中心',
            key: 'mine',
            icon: <SettingOutlined />,
            children: [
                {
                    key: 'my',
                    label: '個人信息',
                },
                {
                    key: 'pwd',
                    label: '修改密碼',
                },
                {
                    key: 'exit',
                    label: '退出系統',
                },
            ],
        }
    ];

    const item2 = [
        {
            key: 'addArticle',
            icon: <FileAddOutlined />,
            label: '發佈文章',
        },
        {
            key: 'yrImage',
            icon: <VideoCameraOutlined />,
            label: '素材管理',
        },
        {
            key: 'yrContent',
            icon: <AlignLeftOutlined />,
            label: '内容管理',
        },
        {
            key: 'yrNotification',
            icon: <NotificationOutlined />,
            label: '通知管理',
        },
        {
            key:'allContent',
            icon:<AlignRightOutlined />,
            label:'文章大全'
        }
    ];


    const onClickMenu = (e) => {
        console.log(e.key)
        setCurrent(e.key)
        //退出
        if (e.key === 'exit'){
            modal.confirm({
                title: '提示',
                icon: <ExclamationCircleOutlined />,
                content: '是否要退出系統？',
                okText: '確認',
                cancelText: '取消',
                onOk() {
                    sessionStorage.clear()
                    localStorage.clear()
                    setTimeout(() => {
                        navigate('/');
                      }, 1000);
                }
            });
        }else if(e.key === 'my'){
            navigate('/layout/myself')
        }else if(e.key === 'pwd'){
            navigate('/layout/changePwd')
        }else if(e.key === 'phone'){
            navigate('/layout/phone')
        }else if(e.key === 'addArticle'){
            navigate('/layout/addArticle')
        }else if(e.key === 'yrContent'){
            navigate('/layout/yrContent')
        }else if(e.key === 'yrImage'){
            navigate('/layout/yrImage')
        }else if(e.key === 'yrNotification'){
            navigate('/layout/yrNotification')
        }else if(e.key === 'home'){
            navigate('/layout/home')
        }else if(e.key == 'allContent'){
            navigate('/layout/allContent')
        }
    }


    return (
        <div >
            {contextHolder}
            <Layout className="layout">
                <Sider trigger={null} collapsible collapsed={collapsed}>
                    <div className="demo-logo-vertical" />
                    <Menu
                        theme="dark"
                        mode="inline"
                        defaultSelectedKeys={['1']}
                        items={item2}
                        onClick={onClickMenu}
                    />
                </Sider>
                <Layout className="right">
                    <Header
                        className="header"
                        style={{
                            padding: 0,
                            background: colorBgContainer,
                        }}
                    >
                        <Menu className="topMenu" onClick={onClickMenu} theme="dark" selectedKeys={[current]} mode="horizontal" items={items} />
                        <Button
                            type="text"
                            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                            onClick={() => setCollapsed(!collapsed)}
                            style={{
                                fontSize: '16px',
                                width: 64,
                                height: 64,
                            }}
                        />



                    </Header>
                    <Content
                        style={{
                            margin: '24px 16px',
                            padding: 24,
                            minHeight: 280,
                            background: colorBgContainer,
                        }}
                    >
                        <Outlet></Outlet>
                    </Content>
                </Layout>
            </Layout>
        </div>
    )
}