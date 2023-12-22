import { LikeOutlined, MessageOutlined, StarOutlined } from '@ant-design/icons';
import React, { useState, useEffect, useLocation } from 'react';
import { Avatar, Button, List, Spin, message, Drawer, Radio, Space } from 'antd';
import { $addLike, $addColletion, $browseList } from '../../api/contentApi'
import { useNavigate } from 'react-router-dom';
import ReactQuill from 'react-quill';
import { $comment } from '../../api/contentApi'

export default function AllContent() {
    let navigate = useNavigate();//導航要放第一
    var userId = localStorage.getItem('id')
    var userName = localStorage.getItem('name')
    console.log('username',userName);
    const [messageApi, contextHolder] = message.useMessage();
    const [resList, setResList] = useState([])

    //----抽屜 start
    const [open, setOpen] = useState(false);
    const [rqContent, setrqContent] = useState('')
    const [temp, setTemp] = useState()
    const showDrawer = (item) => {
        console.log('drawer', item);
        setOpen(true);
        setTemp(item)
    };
    const onChange = (e) => {
        setPlacement(e.target.value);
    };
    const onClose = () => {
        setOpen(false);
    };
    const onSubmit = (c, temp) => {
        //添加评论
        console.log('temp', temp);
        let params = {
            articleId: temp.idStr,
            authorId: temp.authorId,
            commentName: userName,
            comment: c,
            commentId: userId
        }

        $comment(params).then(data => {
            if (data.code == '200') {
                messageApi.open({
                    type: 'success',
                    content: '評論成功',
                });
                setTimeout(() => {
                    $browseList().then(data => {
                        let list = data.data
                        console.log('list', list);
                        setResList(list)
                    })
                }, 1000);
                onClose()
                setrqContent('')
            } else {
                messageApi.open({
                    type: 'error',
                    content: '評論失敗',
                });
            }
        })

    }
    const handleChange = (value) => {
        console.log('value', value);
        setrqContent(value);
    };
    //----抽屜 end


    useEffect(() => {
        $browseList().then(data => {
            let list = data.data
            console.log('list', list);
            setResList(list)
        })
    }, [])

    const IconText = ({ icon, text, flag, articleId, classI }) => {
        const handleClick = (count, flag, articleId, classI) => {
            console.log('count', count);
            console.log('flag', flag);
            console.log("articleId", articleId);
            console.log('classI', classI);

            debugger
            if (flag == 'collection') {
                //收藏
                $addColletion(articleId).then(data => {
                    console.log('data', data);
                    if (data.code == '200') {
                        if (data.data) {
                            messageApi.open({
                                type: 'success',
                                content: '收藏成功',
                            });
                            setTimeout(() => {
                                $browseList().then(data => {
                                    let list = data.data
                                    console.log('list', list);
                                    setResList(list)
                                })
                            }, 1000);
                        } else if (!data.data) {
                            messageApi.open({
                                type: 'success',
                                content: '取消收藏',
                            });
                            setTimeout(() => {
                                $browseList().then(data => {
                                    let list = data.data
                                    console.log('list', list);
                                    setResList(list)
                                })
                            }, 1000);
                        }
                    } else {
                        messageApi.open({
                            type: 'error',
                            content: '收藏失敗',
                        });
                    }

                })

            } else if (flag == 'likes') {
                //點贊
                $addLike(articleId).then(data => {
                    console.log('data', data);
                    if (data.code == '200') {
                        if (data.data) {
                            messageApi.open({
                                type: 'success',
                                content: '點贊成功',
                            });
                            setTimeout(() => {
                                $browseList().then(data => {
                                    let list = data.data
                                    console.log('list', list);
                                    setResList(list)
                                })
                            }, 1000);
                        } else if (!data.data) {
                            messageApi.open({
                                type: 'success',
                                content: '取消點贊',
                            });
                            setTimeout(() => {
                                $browseList().then(data => {
                                    let list = data.data
                                    console.log('list', list);
                                    setResList(list)
                                })
                            }, 1000);
                        }
                    } else {
                        messageApi.open({
                            type: 'error',
                            content: '點贊失敗',
                        });
                    }
                })
            } else if (flag == 'comment') {
                console.log('classI', classI);
            } else if (flag == 'commentList') {
                //評論
                navigate('/layout/yrComment', { state: { res: classI } })
            }
        }

        return (
            <div>
                <Space>
                    <Button onClick={() => handleClick(text, flag, articleId, classI)}>
                        {React.createElement(icon)}
                        {text}
                    </Button>
                </Space>
            </div>
        )
    };





    return (
        <>
            <List
                itemLayout="vertical"
                size="large"
                pagination={{
                    onChange: (page) => {
                        console.log(page);
                    },
                    pageSize: 3,
                }}
                dataSource={resList}

                renderItem={(item) => (
                    <List.Item
                        key={item.idStr}
                        actions={[
                            <IconText icon={StarOutlined}
                                text={item.collection}
                                flag="collection"
                                articleId={item.idStr}
                                classI={item}
                            />,

                            <IconText icon={LikeOutlined}
                                text={item.likes}
                                flag="likes"
                                articleId={item.idStr}
                                classI={item}
                            />,
                            <IconText
                                icon={MessageOutlined}
                                text={item.comment}
                                flag="comment"
                                classI={item}
                            />,
                            <IconText
                                icon={MessageOutlined}
                                text='查看評論'
                                flag="commentList"
                                classI={item}
                            />,
                            <>
                                <Space>
                                    <Radio.Group onChange={onChange}>
                                    </Radio.Group>
                                    <Button type="primary" onClick={() => showDrawer(item)}>
                                        添加評論
                                    </Button>

                                </Space>
                                <Drawer
                                    title="請您填寫評論"
                                    placement='bottom'
                                    width={500}
                                    onClose={onClose}
                                    open={open}
                                    extra={
                                        <Space>
                                            <Button onClick={onClose}>取消</Button>
                                            <Button type="primary" onClick={() => onSubmit(rqContent, temp)}>
                                                提交
                                            </Button>
                                        </Space>
                                    }
                                >
                                    <ReactQuill
                                        className="publish-quill"
                                        theme="snow"
                                        placeholder="請輸入文章内容"
                                        style={{ width: 1200, height: 500 }}
                                        value={rqContent}
                                        onChange={handleChange}
                                    />

                                </Drawer>
                            </>
                        ]}

                    >
                        <List.Item.Meta
                            avatar={<Avatar src={item.avatar} />}
                            title={<a href={item.href}>{item.authorName}</a>}
                            description={<span>發佈于{item.publishTimeStr}</span>}
                        />
                        {item.content}
                    </List.Item>

                )}

            />
            {contextHolder}
        </>

    );
}