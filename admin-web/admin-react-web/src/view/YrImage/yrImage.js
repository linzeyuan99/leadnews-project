import { Button } from "antd";
import React, { useState } from "react";
import { useNavigate } from 'react-router-dom'
import ImgCrop from 'antd-img-crop';
import { Upload } from 'antd';
import { $uploadImg } from '../../api/imageApi'

export default function YrImage() {
    let navigate = useNavigate();//導航要放第一
    let tokenValue = localStorage.getItem('token')
    const urlTo = 'http://127.0.0.1:51601/apiArticle/lin/article/uploadImg?token=' + tokenValue
    const onDic = () => {
        navigate('/layout/editYrImage')
    }

    const [fileList, setFileList] = useState([

    ]);
    const onChange = ({ fileList: newFileList }) => {
        debugger
        console.log('fileList:', fileList);
        console.log("newFileList:", newFileList);
        setFileList(newFileList);
    };
    const onPreview = async (file) => {
        debugger
        let src = file.url;
        console.log('src', src);
        if (!src) {
            src = await new Promise((resolve) => {
                const reader = new FileReader();
                reader.readAsDataURL(file.originFileObj);
                reader.onload = () => resolve(reader.result);
            });
        }
        const image = new Image();
        image.src = src;
        console.log("src", image.src);
        const imgWindow = window.open(src);
        imgWindow?.document.write(image.outerHTML);
    };

    const actionMe = (file) => {
        debugger
        if (file != null) {
            $uploadImg(file).then(data => {
                console.log('data', data);
            })
        }
    }



    return (

        <div>
            <ImgCrop rotationSlider>
                <Upload
                    action={urlTo}
                    listType="picture-card"
                    fileList={fileList}
                    onPreview={onPreview}
                    onChange={onChange}
                >
                    {fileList.length < 5 && '+ Upload'}
                </Upload>

            </ImgCrop>
        </div>

    )
}