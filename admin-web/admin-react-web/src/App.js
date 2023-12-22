import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css';
import Login from './view/Login/Login';
import Layout from './view/Layout/Layout';
import Myself from './view/Myself/Myself';
import ChangePwd from './view/ChangPwd/ChangePwd';
import AddArticle from './view/AddArticle/AddArticle';
import YrImage from './view/YrImage/yrImage';
import EditYrImage from './view/YrImage/editYrImage';
import Home from './view/Home/Home';
import YrContent from './view/YrContent/YrContent';
import EditContent from './view/YrContent/EditContent';
import YrNotification from './view/YrContent/YrNotification';
import AllContent from './view/YrContent/AllContent';
import YrComment from './view/YrContent/YrComment';
import YrPhoneNum from './view/YrPhone/YrPhoneNum';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Login />} />
        <Route path='/layout' element={<Layout />}>
          <Route path='home' element={<Home />} />
          <Route path='myself' element={<Myself />} />
          <Route path='changePwd' element={<ChangePwd />} />
          <Route path='addArticle' element={<AddArticle />} />
          <Route path='yrImage' element={<YrImage />} />
          <Route path='editYrImage' element={<EditYrImage />} />
          <Route path='yrContent' element={<YrContent />} />
          <Route path='yrEditContent' element={<EditContent />} />
          <Route path='yrNotification' element={<YrNotification />} />
          <Route path='allContent' element={<AllContent />} />
          <Route path='yrComment' element={<YrComment />} />
          <Route path='phone' element={<YrPhoneNum />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
